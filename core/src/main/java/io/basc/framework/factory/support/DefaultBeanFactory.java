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
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

@SuppressWarnings({ "unchecked" })
public class DefaultBeanFactory extends DefaultServiceLoaderFactory
		implements ConfigurableBeanFactory, Init, BeanDefinitionLoader {
	private static Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);
	private final DefaultConfigurableAop aop = new DefaultConfigurableAop();
	private final ConfigurableServices<BeanDefinitionLoader> beanDefinitionLoaders = new ConfigurableServices<BeanDefinitionLoader>(
			BeanDefinitionLoader.class);

	private final ConfigurableServices<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ConfigurableServices<BeanFactoryPostProcessor>(
			BeanFactoryPostProcessor.class);
	private volatile boolean initialized = false;

	private BeanFactory parentBeanFactory;

	public DefaultBeanFactory() {
		beanDefinitionLoaders.setAfterService(this);
		aop.addAopPolicy((instance) -> RuntimeBean.getRuntimeBean(instance) != null);
		registerSingleton(BeanFactory.class.getName(), this);
		registerAlias(BeanFactory.class.getName(), InstanceFactory.class.getName());
		getBeanResolver().addService(new InstanceParameterFactory(this));
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

	public void destroy() {
		synchronized (this) {
			if (!isInitialized()) {
				throw new FactoryException("The bean factory has not been initialized");
			}

			logger.debug("Start destroy bean factory[{}]!", this);
			super.destroy();
			// TODO 是否需要解绑FactoryLoader
			logger.debug("Destroyed bean factory[{}]!", this);
		}
	}

	public ConfigurableAop getAop() {
		return aop;
	}

	public ConfigurableServices<BeanDefinitionLoader> getBeanDefinitionLoaders() {
		return beanDefinitionLoaders;
	}

	public ConfigurableServices<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
		return beanFactoryPostProcessors;
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
	public <T> T getInstance(Class<? extends T> clazz) throws FactoryException {
		if (parentBeanFactory != null && !containsDefinition(clazz.getName()) && !super.isInstance(clazz)
				&& parentBeanFactory.isInstance(clazz)) {
			return parentBeanFactory.getInstance(clazz);
		}
		return super.getInstance(clazz);
	}

	@Override
	public <T> T getInstance(Class<? extends T> type, Class<?>[] parameterTypes, Object... params) {
		if (parentBeanFactory != null && !containsDefinition(type.getName())
				&& !isInstance(type.getName(), (e) -> e.isInstance(parameterTypes))
				&& parentBeanFactory.isInstance(type, parameterTypes)) {
			return parentBeanFactory.getInstance(type, parameterTypes, params);
		}
		return (T) getInstance(type.getName(), parameterTypes, params);
	}

	@Override
	public <T> T getInstance(Class<? extends T> type, Object... params) {
		if (parentBeanFactory != null && !containsDefinition(type.getName())
				&& !isInstance(type.getName(), (e) -> e.isInstance(params))
				&& parentBeanFactory.isInstance(type, params)) {
			return parentBeanFactory.getInstance(type, params);
		}
		return (T) getInstance(type.getName(), params);
	}

	@Override
	public Object getInstance(String name) throws FactoryException {
		if (parentBeanFactory != null && !containsDefinition(name) && !super.isInstance(name)
				&& parentBeanFactory.isInstance(name)) {
			return parentBeanFactory.getInstance(name);
		}
		return super.getInstance(name);
	}

	@Override
	public Object getInstance(String name, Class<?>[] parameterTypes, Object... params) {
		if (parentBeanFactory != null && !containsDefinition(name)
				&& !isInstance(name, (e) -> e.isInstance(parameterTypes))
				&& parentBeanFactory.isInstance(name, parameterTypes)) {
			return parentBeanFactory.getInstance(name, parameterTypes, params);
		}
		return getInstance(name, (e) -> e.create(parameterTypes, params));
	}

	@Override
	public Object getInstance(String name, Object... params) {
		if (parentBeanFactory != null && !containsDefinition(name) && !isInstance(name, (e) -> e.isInstance(params))
				&& parentBeanFactory.isInstance(name, params)) {
			return parentBeanFactory.getInstance(name, params);
		}
		return getInstance(name, (e) -> e.create(params));
	}

	@Override
	public BeanFactory getParentBeanFactory() {
		return parentBeanFactory;
	}

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

	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public boolean isInstance(Class<?> clazz) {
		return super.isInstance(clazz) || (parentBeanFactory != null && !containsDefinition(clazz.getName())
				&& parentBeanFactory.isInstance(clazz));
	}

	@Override
	public boolean isInstance(Class<?> clazz, Class<?>... parameterTypes) {
		return isInstance(clazz.getName(), parameterTypes) || (parentBeanFactory != null
				&& !containsDefinition(clazz.getName()) && parentBeanFactory.isInstance(clazz, parameterTypes));
	}

	public boolean isInstance(Class<?> clazz, Object... params) {
		return isInstance(clazz.getName(), params) || (parentBeanFactory != null && !containsDefinition(clazz.getName())
				&& parentBeanFactory.isInstance(clazz, params));
	}

	@Override
	public boolean isInstance(String name) {
		return super.isInstance(name)
				|| (parentBeanFactory != null && !containsDefinition(name) && parentBeanFactory.isInstance(name));
	}

	@Override
	public boolean isInstance(String name, Class<?>... parameterTypes) {
		return isInstance(name, (e) -> e.isInstance(parameterTypes)) || (parentBeanFactory != null
				&& !containsDefinition(name) && parentBeanFactory.isInstance(name, parameterTypes));
	}

	public boolean isInstance(String name, Object... params) {
		return isInstance(name, (e) -> e.isInstance()) || (parentBeanFactory != null && !containsDefinition(name)
				&& parentBeanFactory.isInstance(name, params));
	}

	@Override
	public BeanDefinition load(BeanFactory beanFactory, String name, BeanDefinitionLoaderChain chain)
			throws FactoryException {
		return chain.load(beanFactory, name);
	}

	@Override
	public BeanDefinition register(String id, TypeDescriptor typeDescriptor, boolean singleton,
			Supplier<Boolean> isInstanceSupplier, Supplier<?> supplier) {
		return registerDefinition(
				new RegisterBeanDefinition(this, typeDescriptor, id, singleton, isInstanceSupplier, supplier));
	}

	@Override
	public void setClassLoader(ClassLoader classLoader) {
		if (isInitialized()) {
			throw new FactoryException("Already initialized");
		}
		super.setClassLoader(classLoader);
		setParentBeanFactory(FactoryLoader.bind(classLoader, this));
	}

	public void setParentBeanFactory(BeanFactory parentBeanFactory) {
		FactoryLoader.getParentBeanFactory(parentBeanFactory, this).assertSuccess();
		this.parentBeanFactory = parentBeanFactory;
	}
}
