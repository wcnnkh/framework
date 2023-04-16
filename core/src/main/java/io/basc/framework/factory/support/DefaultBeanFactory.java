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

	private BeanFactory parent;

	public DefaultBeanFactory() {
		beanDefinitionLoaders.registerLast(this);
		aop.addAopPolicy((instance) -> RuntimeBean.getRuntimeBean(instance) != null);
		registerSingleton(BeanFactory.class.getName(), this);
		registerAlias(BeanFactory.class.getName(), InstanceFactory.class.getName());
		getBeanResolver().register(new InstanceParameterFactory(this));
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

	public BeanDefinition getDefinition(ClassLoader classLoader, String name) {
		BeanDefinition definition = super.getDefinition(name);
		if (definition == null) {
			synchronized (getDefinitionMutex()) {
				definition = super.getDefinition(name);
				if (definition == null) {
					definition = new BeanDefinitionLoaderChain(this.beanDefinitionLoaders.iterator()).load(this,
							classLoader, name);
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
	public final BeanDefinition getDefinition(String name) {
		return getDefinition(getClassLoader(), name);
	}

	@Override
	public final BeanDefinition getDefinition(Class<?> clazz) {
		BeanDefinition definition = super.getDefinition(clazz);
		if (definition != null) {
			return definition;
		}
		return getDefinition(clazz.getClassLoader(), clazz.getName());
	}

	@Override
	public <T> T getInstance(Class<? extends T> clazz) throws FactoryException {
		if (parent != null && !containsDefinition(clazz.getName()) && !super.isInstance(clazz)
				&& parent.isInstance(clazz)) {
			return parent.getInstance(clazz);
		}
		return super.getInstance(clazz);
	}

	@Override
	public <T> T getInstance(Class<? extends T> type, Class<?>[] parameterTypes, Object... params) {
		if (parent != null && !containsDefinition(type.getName())
				&& !isInstance(type.getName(), (e) -> e.isInstance(parameterTypes))
				&& parent.isInstance(type, parameterTypes)) {
			return parent.getInstance(type, parameterTypes, params);
		}
		return (T) getInstance(type.getName(), parameterTypes, params);
	}

	@Override
	public <T> T getInstance(Class<? extends T> type, Object... params) {
		if (parent != null && !containsDefinition(type.getName())
				&& !isInstance(type.getName(), (e) -> e.isInstance(params)) && parent.isInstance(type, params)) {
			return parent.getInstance(type, params);
		}
		return (T) getInstance(type.getName(), params);
	}

	@Override
	public Object getInstance(String name) throws FactoryException {
		if (parent != null && !containsDefinition(name) && !super.isInstance(name) && parent.isInstance(name)) {
			return parent.getInstance(name);
		}
		return super.getInstance(name);
	}

	@Override
	public Object getInstance(String name, Class<?>[] parameterTypes, Object... params) {
		if (parent != null && !containsDefinition(name) && !isInstance(name, (e) -> e.isInstance(parameterTypes))
				&& parent.isInstance(name, parameterTypes)) {
			return parent.getInstance(name, parameterTypes, params);
		}
		return getInstance(name, (e) -> e.create(parameterTypes, params));
	}

	@Override
	public Object getInstance(String name, Object... params) {
		if (parent != null && !containsDefinition(name) && !isInstance(name, (e) -> e.isInstance(params))
				&& parent.isInstance(name, params)) {
			return parent.getInstance(name, params);
		}
		return getInstance(name, (e) -> e.create(params));
	}

	@Override
	public BeanFactory getParent() {
		return parent;
	}

	@Override
	public void init() {
		synchronized (this) {
			if (isInitialized()) {
				throw new FactoryException("The bean factory has been initialized");
			}

			try {
				logger.debug("Start initializing bean factory[{}]!", this);

				if (parent == null) {
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
		return super.isInstance(clazz)
				|| (parent != null && !containsDefinition(clazz.getName()) && parent.isInstance(clazz));
	}

	@Override
	public boolean isInstance(Class<?> clazz, Class<?>... parameterTypes) {
		return isInstance(clazz.getName(), parameterTypes)
				|| (parent != null && !containsDefinition(clazz.getName()) && parent.isInstance(clazz, parameterTypes));
	}

	public boolean isInstance(Class<?> clazz, Object... params) {
		return isInstance(clazz.getName(), params)
				|| (parent != null && !containsDefinition(clazz.getName()) && parent.isInstance(clazz, params));
	}

	@Override
	public boolean isInstance(String name) {
		return super.isInstance(name) || (parent != null && !containsDefinition(name) && parent.isInstance(name));
	}

	@Override
	public boolean isInstance(String name, Class<?>... parameterTypes) {
		return isInstance(name, (e) -> e.isInstance(parameterTypes))
				|| (parent != null && !containsDefinition(name) && parent.isInstance(name, parameterTypes));
	}

	public boolean isInstance(String name, Object... params) {
		return isInstance(name, (e) -> e.isInstance())
				|| (parent != null && !containsDefinition(name) && parent.isInstance(name, params));
	}

	@Override
	public BeanDefinition load(BeanFactory beanFactory, ClassLoader classLoader, String name,
			BeanDefinitionLoaderChain chain) throws FactoryException {
		return chain.load(beanFactory, classLoader, name);
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

	public void setParentBeanFactory(BeanFactory parent) {
		FactoryLoader.getParentBeanFactory(parent, this).assertSuccess();
		this.parent = parent;
	}
}
