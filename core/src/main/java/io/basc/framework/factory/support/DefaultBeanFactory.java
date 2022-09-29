package io.basc.framework.factory.support;

import java.util.function.Supplier;

import io.basc.framework.aop.ConfigurableAop;
import io.basc.framework.aop.support.DefaultConfigurableAop;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeanFactoryAware;
import io.basc.framework.factory.BeanFactoryPostProcessor;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableBeanFactory;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.FactoryLoader;
import io.basc.framework.factory.Init;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.annotation.AnnotationFactoryInstanceResolverExtend;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;

@SuppressWarnings({ "unchecked" })
public class DefaultBeanFactory extends DefaultServiceLoaderFactory
		implements ConfigurableBeanFactory, Init, BeanDefinitionLoader {
	private static Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);
	private final DefaultConfigurableAop aop = new DefaultConfigurableAop();
	private final ConfigurableServices<BeanDefinitionLoader> beanDefinitionLoaders = new ConfigurableServices<BeanDefinitionLoader>(
			BeanDefinitionLoader.class);

	private final ConfigurableServices<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ConfigurableServices<BeanFactoryPostProcessor>(
			BeanFactoryPostProcessor.class);
	private BeanFactory parentBeanFactory;

	public DefaultBeanFactory() {
		beanDefinitionLoaders.setAfterService(this);
		aop.addAopPolicy((instance) -> RuntimeBean.getRuntimeBean(instance) != null);
		registerSingleton(BeanFactory.class.getName(), this);
		registerAlias(BeanFactory.class.getName(), InstanceFactory.class.getName());
		getBeanResolver().addService(new AnnotationFactoryInstanceResolverExtend(this));
	}

	@Override
	public void setClassLoader(ClassLoader classLoader) {
		super.setClassLoader(classLoader);
		setParentBeanFactory(FactoryLoader.bind(classLoader, this));
	}

	public void setParentBeanFactory(BeanFactory parentBeanFactory) {
		Assert.isTrue(FactoryLoader.getParentBeanFactory(parentBeanFactory, this).isActive(),
				"BeanFactory cannot be nested circularly");
		this.parentBeanFactory = parentBeanFactory;
	}

	@Override
	public BeanFactory getParentBeanFactory() {
		return parentBeanFactory;
	}

	public ConfigurableServices<BeanDefinitionLoader> getBeanDefinitionLoaders() {
		return beanDefinitionLoaders;
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
		if (instance instanceof Configurable && !((Configurable) instance).isConfigured()) {
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
				logger.debug("Start initializing bean factory[{}]!", this);

				if (parentBeanFactory == null) {
					setParentBeanFactory(FactoryLoader.bind(getClassLoader(), this));
				}

				if (!beanFactoryPostProcessors.isConfigured()) {
					beanFactoryPostProcessors.configure(this);
				}

				if (!getBeanResolver().isConfigured()) {
					getBeanResolver().configure(this);
				}

				if (!beanDefinitionLoaders.isConfigured()) {
					beanDefinitionLoaders.configure(this);
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
	public BeanDefinition getDefinition(String name) {
		BeanDefinition definition = super.getDefinition(name);
		if (definition == null) {
			synchronized (getDefinitionMutex()) {
				definition = super.getDefinition(name);
				if (definition == null) {
					definition = new BeanDefinitionLoaderChain(this.beanDefinitionLoaders.iterator()).load(this, name);
					if (definition == null || !definition.isInstance()) {
						for (String aliase : getAliases(name)) {
							BeanDefinition aliaseDefinition = getDefinition(aliase);
							if (aliaseDefinition != null && aliaseDefinition.isInstance()) {
								definition = aliaseDefinition;
								break;
							}
						}
					}

					if (definition != null) {
						definition = registerDefinition(name, definition);
					}
				}
			}
		}
		return definition;
	}

	@Override
	public BeanDefinition load(BeanFactory beanFactory, String name, BeanDefinitionLoaderChain chain)
			throws FactoryException {
		return chain.load(beanFactory, name);
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
	public Object getInstance(String name, Class<?>[] parameterTypes, Object... params) {
		return getInstance(name, (e) -> e.create(parameterTypes, params));
	}

	@Override
	public Object getInstance(String name, Object... params) {
		return getInstance(name, (e) -> e.create(params));
	}

	@Override
	public boolean isInstance(Class<?> clazz, Class<?>... parameterTypes) {
		return isInstance(clazz.getName(), parameterTypes);
	}

	public boolean isInstance(Class<?> clazz, Object... params) {
		return isInstance(clazz.getName(), params);
	}

	@Override
	public boolean isInstance(String name, Class<?>... parameterTypes) {
		return isInstance(name, (e) -> e.isInstance(parameterTypes));
	}

	public boolean isInstance(String name, Object... params) {
		return isInstance(name, (e) -> e.isInstance(params));
	}

	@Override
	public BeanDefinition register(String id, TypeDescriptor typeDescriptor, boolean singleton,
			Supplier<Boolean> isInstanceSupplier, Supplier<?> supplier) {
		return registerDefinition(
				new RegisterBeanDefinition(this, typeDescriptor, id, singleton, isInstanceSupplier, supplier));
	}
}
