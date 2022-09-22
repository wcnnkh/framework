package io.basc.framework.factory.support;

import java.util.function.Supplier;

import io.basc.framework.aop.ConfigurableAop;
import io.basc.framework.aop.support.DefaultConfigurableAop;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanDefinitionFactory;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeanFactoryAware;
import io.basc.framework.factory.BeanFactoryPostProcessor;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableBeanFactory;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.Init;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.stream.CallableProcessor;
import io.basc.framework.util.stream.Processor;

@SuppressWarnings({ "unchecked" })
public class DefaultBeanFactory extends DefaultSingletonRegistry implements ConfigurableBeanFactory, Init {
	private static Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);
	private final DefaultConfigurableAop aop = new DefaultConfigurableAop();
	private final ConfigurableServices<BeanDefinitionLoader> beanDefinitionLoaders = new ConfigurableServices<BeanDefinitionLoader>(
			BeanDefinitionLoader.class);

	private ClassLoaderProvider classLoaderProvider;
	private final ConfigurableServices<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ConfigurableServices<BeanFactoryPostProcessor>();

	public DefaultBeanFactory() {
		this(null);
	}

	public DefaultBeanFactory(BeanDefinitionFactory parentBeanDefinitionFactory) {
		super(parentBeanDefinitionFactory);
		aop.addAopPolicy((instance) -> RuntimeBean.getRuntimeBean(instance) != null);
		registerSingleton(BeanFactory.class.getName(), this);
		registerAlias(BeanFactory.class.getName(), InstanceFactory.class.getName());
	}

	@Override
	protected void _dependence(Object instance, BeanDefinition definition) throws FactoryException {
		super._dependence(instance, definition);
		if (instance instanceof BeanFactoryAware) {
			((BeanFactoryAware) instance).setBeanFactory(this);
		}
	}

	@Override
	protected void _init(Object instance, BeanDefinition definition) throws FactoryException {
		if (instance instanceof Configurable) {
			((Configurable) instance).configure(this);
		}
		super._init(instance, definition);
	}

	private volatile boolean initialized = false;

	@Override
	public void init() {
		synchronized (this) {
			if (isInitialized()) {
				throw new FactoryException("The bean factory has been initialized");
			}

			try {
				FactoryLoader.bindBeanFactory(this);
				logger.debug("Start initializing bean factory[{}]!", this);
				if (!beanFactoryPostProcessors.isConfigured()) {
					beanFactoryPostProcessors.configure(this);
				}

				if (getBeanResolver().isConfigured()) {
					getBeanResolver().configure(this);
				}

				for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
					postProcessor.postProcessBeanFactory(this);
				}

				aop.configure(this);
				logger.debug("Started bean factory[{}]!", this);
			} finally {
				initialized = true;
			}
		}
	}

	public ConfigurableServices<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
		return beanFactoryPostProcessors;
	}

	public void destroy() {
		synchronized (this) {
			if (!isInitialized()) {
				throw new FactoryException("The bean factory has not been initialized");
			}

			try {
				logger.debug("Start destroy bean factory[{}]!", this);
				super.destroy();
				logger.debug("Destroyed bean factory[{}]!", this);
			} finally {
				initialized = false;
			}
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

	public ConfigurableAop getAop() {
		return aop;
	}

	@Override
	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}

	public ClassLoaderProvider getClassLoaderProvider() {
		return classLoaderProvider;
	}

	@Override
	public BeanDefinition getDefinition(String name) {
		BeanDefinition definition = super.getDefinition(name);
		if (definition == null) {
			synchronized (getDefinitionMutex()) {
				definition = new BeanDefinitionLoaderChain(this.beanDefinitionLoaders.iterator()).load(this, name);
				if (definition != null) {
					registerDefinition(name, definition);
				}
			}
		}
		return definition;
	}

	public <T, E extends Throwable> T getInstance(BeanDefinition definition, CallableProcessor<T, E> creater) throws E {
		if (definition.isSingleton()) {
			return getSingleton(definition.getId(), creater).get();
		}

		T instance = creater.process();
		processPostBean(instance, definition);
		return instance;
	}

	@Override
	public <T> T getInstance(Class<? extends T> clazz) {
		return (T) getInstance(clazz.getName());
	}

	@Override
	public <T> T getInstance(Class<? extends T> type, Class<?>[] parameterTypes, Object... params) {
		return (T) getInstance(type.getName(), parameterTypes, params);
	}

	@Override
	public <T> T getInstance(Class<? extends T> type, Object... params) {
		return (T) getInstance(type.getName(), params);
	}

	@Override
	public Object getInstance(String name) {
		return getInstance(name, (e) -> e.create());
	}

	@Override
	public Object getInstance(String name, Class<?>[] parameterTypes, Object... params) {
		return getInstance(name, (e) -> e.create(parameterTypes, params));
	}

	@Override
	public Object getInstance(String name, Object... params) {
		return getInstance(name, (e) -> e.create(params));
	}

	public <T, E extends Throwable> T getInstance(String name, Processor<BeanDefinition, T, E> createProcessor)
			throws E {
		Object object = getSingleton(name);
		if (object != null) {
			return (T) object;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		return getInstance(definition, () -> createProcessor.process(definition));
	}

	public boolean isInstance(Class<?> clazz) {
		return isInstance(clazz.getName());
	}

	@Override
	public boolean isInstance(Class<?> clazz, Class<?>... parameterTypes) {
		return isInstance(clazz.getName(), parameterTypes);
	}

	public boolean isInstance(Class<?> clazz, Object... params) {
		return isInstance(clazz.getName(), params);
	}

	public boolean isInstance(String name) {
		if (containsSingleton(name)) {
			return true;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return containsSingleton(definition.getId()) || definition.isInstance();
	}

	@Override
	public boolean isInstance(String name, Class<?>... parameterTypes) {
		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return definition.isInstance(parameterTypes);
	}

	public boolean isInstance(String name, Object... params) {
		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return definition.isInstance(params);
	}

	public boolean isSingleton(Class<?> clazz) {
		return isSingleton(clazz.getName());
	}

	@Override
	public boolean isSingleton(String name) {
		if (containsSingleton(name)) {
			return true;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return containsSingleton(definition.getId()) || definition.isSingleton();
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	@Override
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		return new SpiServiceLoader<S>(serviceClass, this);
	}

	@Override
	public BeanDefinition register(String id, TypeDescriptor typeDescriptor, boolean singleton,
			Supplier<Boolean> isInstanceSupplier, Supplier<?> supplier) {
		return registerDefinition(
				new RegisterBeanDefinition(this, typeDescriptor, id, singleton, isInstanceSupplier, supplier));
	}
}
