package io.basc.framework.beans.factory.support;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.FatalBeanException;
import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.beans.factory.NoSuchBeanDefinitionException;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanFactoryPostProcessor;
import io.basc.framework.beans.factory.config.BeanPostProcessorRegistry;
import io.basc.framework.beans.factory.config.ConfigurableBeanFactory;
import io.basc.framework.beans.factory.config.DisposableBean;
import io.basc.framework.beans.factory.config.InitializingBean;
import io.basc.framework.beans.factory.config.LifecycleFactoryBean;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ServiceRegistry;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {
	private static Logger logger = LoggerFactory.getLogger(AbstractBeanFactory.class);
	private final ConcurrentHashMap<String, FactoryBean<? extends Object>> factoryBeanMap = new ConcurrentHashMap<>();
	private final BeanPostProcessorRegistry beanPostProcessorRegistry = new BeanPostProcessorRegistry();
	private final ServiceRegistry<BeanFactoryPostProcessor> beanFactoryPostProcessorRegistry = new ServiceRegistry<>();

	public AbstractBeanFactory() {
		registerSingleton(BeanFactory.class.getSimpleName(), this);
	}

	@Override
	public BeanPostProcessorRegistry getBeanPostProcessorRegistry() {
		return beanPostProcessorRegistry;
	}

	public FactoryBean<? extends Object> getFactoryBean(String beanName) throws NoSuchBeanDefinitionException {
		FactoryBean<? extends Object> factoryBean = factoryBeanMap.get(beanName);
		if (factoryBean == null) {
			for (String alias : getAliases(beanName)) {
				factoryBean = factoryBeanMap.get(alias);
				if (factoryBean != null) {
					return factoryBean;
				}
			}
		}

		if (factoryBean == null) {
			throw new NoSuchBeanDefinitionException(beanName);
		}

		return factoryBean;
	}

	public void removeFactoryBean(String beanName) throws BeansException {
		if (factoryBeanMap.remove(beanName) != null) {
			// 移除成功
			return;
		}

		for (String alias : getAliases(beanName)) {
			if (factoryBeanMap.remove(alias) != null) {
				// 移除成功
				return;
			}
		}
		// 不存在
		throw new NoSuchBeanDefinitionException(beanName);
	}

	public Elements<String> getFactoryBeanNames() {
		return Elements.of(factoryBeanMap.keySet());
	}

	@Override
	public boolean isFactoryBean(String beanName) {
		if (factoryBeanMap.contains(beanName)) {
			return true;
		}

		for (String alias : getAliases(beanName)) {
			if (factoryBeanMap.contains(alias)) {
				return true;
			}
		}
		return containsBeanDefinition(beanName);
	}

	@Override
	public void registerFactoryBean(String beanName, FactoryBean<? extends Object> factoryBean) throws BeansException {
		if (factoryBeanMap.putIfAbsent(beanName, factoryBean) != null) {
			throw new BeansException("Existing factory beans '" + beanName + "'");
		}
	}

	@Override
	public Object getSingleton(String name) throws BeansException {
		Object singleton = super.getSingleton(name);
		if (singleton == null) {
			if (containsBean(name)) {
				return null;
			}

			FactoryBean<? extends Object> factoryBean = getFactoryBean(name);
			if (factoryBean == null) {
				return null;
			}

			Lock writeLock = getReadWriteLock().writeLock();
			writeLock.lock();
			boolean newSingleton = false;
			try {
				singleton = super.getSingleton(name);
				if (singleton == null) {
					if (containsSingleton(name)) {
						return null;
					}

					factoryBean = getFactoryBean(name);
					if (factoryBean != null && factoryBean.isSingleton()) {
						singleton = factoryBean.get();
						registerSingleton(name, singleton);
						newSingleton = true;
					}
				}
			} finally {
				writeLock.unlock();
			}

			if (newSingleton) {
				// 新的单例，初始化一下
				initializationBean(name, singleton);
			}
		}
		return singleton;
	}

	@Override
	public void removeSingleton(String name) throws BeansException {
		Lock writeLock = getReadWriteLock().writeLock();
		writeLock.lock();
		try {
			// 从父类获取，不主动加载单例
			Object singletonBean = super.getSingleton(name);
			if (singletonBean != null) {
				destroyBean(name, singletonBean);
			}
			super.removeSingleton(name);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void destroySingletons() {
		// 获取注册时顺序的倒序，先注册的后销毁
		for (String singletonName : getRegistrationOrderSingletonNames().reverse()) {
			if (!isFactoryBean(singletonName)) {
				// 非工厂bean不销毁
				continue;
			}

			try {
				removeSingleton(singletonName);
			} catch (Throwable e) {
				logger.error(e, "Exception in destroying a single instance '{}'", singletonName);
			}
		}
	}

	@Override
	public boolean isSingleton(String beanName) throws BeansException {
		if (containsSingleton(beanName)) {
			return true;
		}

		BeanDefinition beanDefinition = getBeanDefinition(beanName);
		if (beanDefinition != null) {
			return beanDefinition.isSingleton();
		}

		FactoryBean<? extends Object> factoryBean = getFactoryBean(beanName);
		if (factoryBean == null) {
			throw new NoSuchBeanDefinitionException(beanName);
		}

		return factoryBean.isSingleton();
	}

	@Override
	public Object getBean(String beanName) throws BeansException {
		if (isSingleton(beanName)) {
			return getSingleton(beanName);
		}

		FactoryBean<? extends Object> factoryBean = getFactoryBean(beanName);
		if (factoryBean == null) {
			throw new NoSuchBeanDefinitionException(beanName);
		}

		Object bean = factoryBean.get();
		initializationBean(beanName, bean);
		return bean;
	}

	@Override
	public Elements<String> getBeanNames() {
		return getRegistrationOrderSingletonNames().concat(getFactoryBeanNames()).concat(getBeanDefinitionNames())
				.distinct();
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		if (!isFactoryBean(name) && super.containsSingleton(name)) {
			Object singleton = super.getSingleton(name);
			return typeToMatch.isInstance(singleton);
		}

		FactoryBean<? extends Object> factoryBean = getFactoryBean(name);
		if (factoryBean == null) {
			throw new NoSuchBeanDefinitionException(name);
		}

		return factoryBean.getType().isAssignableFrom(typeToMatch);
	}

	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		if (!isFactoryBean(name) && super.containsSingleton(name)) {
			Object singleton = super.getSingleton(name);
			ResolvableType sourceType = singleton == null ? ResolvableType.NONE
					: ResolvableType.forClass(singleton.getClass());
			return sourceType.isAssignableFrom(sourceType);
		}

		FactoryBean<? extends Object> factoryBean = getFactoryBean(name);
		if (factoryBean == null) {
			throw new NoSuchBeanDefinitionException(name);
		}

		return factoryBean.getType().isAssignableFrom(typeToMatch);
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		if (!isFactoryBean(name) && super.containsSingleton(name)) {
			Object singleton = super.getSingleton(name);
			return singleton == null ? Object.class : singleton.getClass();
		}

		FactoryBean<? extends Object> factoryBean = getFactoryBean(name);
		if (factoryBean == null) {
			throw new NoSuchBeanDefinitionException(name);
		}

		return factoryBean.getType().getRawClass();
	}

	@Override
	public final boolean containsBean(String name) {
		return containsSingleton(name) || isFactoryBean(name);
	}

	protected void dependence(Object bean, String beanName) throws BeansException {
		FactoryBean<? extends Object> factoryBean = getFactoryBean(beanName);
		if (factoryBean != null) {
			if (factoryBean instanceof LifecycleFactoryBean) {
				((LifecycleFactoryBean<?>) factoryBean).dependence(factoryBean);
			}
		}
	}

	protected void init(Object bean, String beanName) throws BeansException {
		FactoryBean<? extends Object> factoryBean = getFactoryBean(beanName);
		if (factoryBean != null) {
			if (factoryBean instanceof LifecycleFactoryBean) {
				((LifecycleFactoryBean<?>) factoryBean).init(bean);
			}
		}

		if (bean instanceof InitializingBean) {
			try {
				((InitializingBean) bean).afterPropertiesSet();
			} catch (Exception e) {
				throw new FatalBeanException(beanName, e);
			}
		}
	}

	@Override
	public void initializationBean(String beanName, Object bean) throws BeansException {
		if (!isFactoryBean(beanName)) {
			// 只有工厂bean才存在生命周期
			return;
		}

		beanPostProcessorRegistry.postProcessBeforeDependencies(bean, beanName);
		dependence(bean, beanName);
		beanPostProcessorRegistry.postProcessAfterDependencies(bean, beanName);

		beanPostProcessorRegistry.postProcessBeforeDependencies(bean, beanName);
		init(bean, beanName);
		beanPostProcessorRegistry.postProcessAfterDependencies(bean, beanName);
	}

	protected void destroy(Object bean, String beanName) throws BeansException {
		if (bean instanceof DisposableBean) {
			try {
				((DisposableBean) bean).destroy();
			} catch (Throwable e) {
				logger.error(e, "An exception occurred while xxx bean was executing xxx DisposableBean#destroy");
			}
		}

		FactoryBean<? extends Object> factoryBean = getFactoryBean(beanName);
		if (factoryBean != null) {
			if (factoryBean instanceof LifecycleFactoryBean) {
				((LifecycleFactoryBean<?>) factoryBean).destroy(bean);
			}
		}
	}

	@Override
	public void destroyBean(String beanName, Object bean) throws BeansException {
		if (!isFactoryBean(beanName)) {
			// 只有工厂bean才存在生命周期
			return;
		}

		beanPostProcessorRegistry.postProcessBeforeDestory(bean, beanName);
		dependence(bean, beanName);
		beanPostProcessorRegistry.postProcessAfterDestory(bean, beanName);
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException {
		A annotation = ConfigurableBeanFactory.super.findAnnotationOnBean(beanName, annotationType);
		if (annotation != null) {
			return annotation;
		}

		FactoryBean<? extends Object> factoryBean = getFactoryBean(beanName);
		if (factoryBean instanceof DefinitionFactoryBean) {
			DefinitionFactoryBean definitionFactoryBean = (DefinitionFactoryBean) factoryBean;
			return definitionFactoryBean.getExecutor().getTypeDescriptor().getAnnotation(annotationType);
		}
		return null;
	}

	/**
	 * 准备好所有单例
	 */
	public void prepareAllSingletons() throws BeansException {
		for (String name : getBeanNames()) {
			if (isSingleton(name)) {
				// 获取单例
				getSingleton(name);
			}
		}
	}

	private volatile boolean initialized = false;

	public boolean isInitialized() {
		return initialized;
	}

	public final void init() {
		synchronized (this) {
			if (initialized) {
				throw new IllegalStateException("Already initialized [" + this + "]");
			}

			try {
				_init();
			} finally {
				initialized = true;
			}
		}
	}

	protected void _init() {
		for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorRegistry.getServices()) {
			beanFactoryPostProcessor.postProcessBeanFactory(this);
		}
	}
}
