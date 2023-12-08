package io.basc.framework.beans.factory.support;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.FatalBeanException;
import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.BeanFactoryAware;
import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.beans.factory.HierarchicalBeanFactory;
import io.basc.framework.beans.factory.NoSuchBeanDefinitionException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanPostProcessors;
import io.basc.framework.beans.factory.config.ConfigurableBeanFactory;
import io.basc.framework.beans.factory.config.DisposableBean;
import io.basc.framework.beans.factory.config.InitializingBean;
import io.basc.framework.beans.factory.config.LifecycleFactoryBean;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.param.ExtractParameterException;
import io.basc.framework.execution.param.Parameter;
import io.basc.framework.execution.param.ParameterExtractors;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.observe.register.ServiceInjectors;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.Elements;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {
	private static Logger logger = LoggerFactory.getLogger(AbstractBeanFactory.class);
	private final BeanPostProcessors beanPostProcessors = new BeanPostProcessors();
	private final ConcurrentHashMap<String, FactoryBean<? extends Object>> factoryBeanMap = new ConcurrentHashMap<>();
	private final Set<String> initializedSingletonSet = new HashSet<>();
	private final ParameterExtractors<BeanFactory> parameterExtractors = new ParameterExtractors<>();
	private BeanFactory parentBeanFactory;
	private final Scope scope;
	private final ServiceInjectors<Object> serviceInjectors = new ServiceInjectors<>();

	public AbstractBeanFactory(Scope scope) {
		Assert.requiredArgument(scope != null, "scope");
		this.scope = scope;
		registerSingleton(BeanFactory.class.getSimpleName(), this);
		beanPostProcessors.getServiceInjectors().register(serviceInjectors);

		serviceInjectors.register((bean) -> {
			if (bean instanceof BeanFactoryAware) {
				((BeanFactoryAware) bean).setBeanFactory(this);
			}
			return Registration.EMPTY;
		});
	}

	protected void _destroyBean(String beanName, Object bean) throws BeansException {
		beanPostProcessors.postProcessBeforeDestroy(bean, beanName);
		destroy(bean, beanName);
		beanPostProcessors.postProcessAfterDestroy(bean, beanName);
	}

	protected void _initializationBean(String beanName, Object bean) throws BeansException {
		beanPostProcessors.postProcessBeforeInitialization(bean, beanName);
		init(bean, beanName);
		// 注入
		serviceInjectors.inject(bean);
		beanPostProcessors.postProcessAfterInitialization(bean, beanName);
	}

	@Override
	public boolean canExtractParameter(ParameterDescriptor parameterDescriptor) {
		return parameterExtractors.canExtractParameter(this, parameterDescriptor);
	}

	@Override
	public boolean canExtractParameters(Elements<? extends ParameterDescriptor> parameterDescriptors) {
		return parameterExtractors.canExtractParameters(this, parameterDescriptors);
	}

	@Override
	public boolean canExtractParameters(Executor executor) {
		return parameterExtractors.canExtractParameters(this, executor);
	}

	@Override
	public final boolean containsBean(String name) {
		return containsSingleton(name) || isFactoryBean(name);
	}

	@Override
	public boolean containsLocalBean(String name) {
		return containsSingleton(name) || isFactoryBean(name);
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

		if (isSingleton(beanName)) {
			Lock writeLock = getReadWriteLock().writeLock();
			writeLock.lock();
			try {
				if (!removeInitializedSingleton(beanName)) {
					throw new BeansException("Not initialized yet '" + beanName + "'");
				}

				_destroyBean(beanName, bean);
			} finally {
				writeLock.unlock();
			}
		} else {
			_destroyBean(beanName, bean);
		}
	}

	@Override
	public void destroySingletons() {
		// 获取注册时顺序的倒序，先注册的后销毁
		for (String singletonName : getRegistrationOrderSingletonNames().reverse()) {
			if (!isFactoryBean(singletonName) && containsLocalBean(singletonName)) {
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
	public Parameter extractParameter(ParameterDescriptor parameterDescriptor) throws ExtractParameterException {
		return parameterExtractors.extractParameter(this, parameterDescriptor);
	}

	@Override
	public Elements<Parameter> extractParameters(Elements<? extends ParameterDescriptor> parameterDescriptors)
			throws ExtractParameterException {
		return parameterExtractors.extractParameters(this, parameterDescriptors);
	}

	@Override
	public Parameters extractParameters(Executor executor) throws ExtractParameterException {
		return parameterExtractors.extractParameters(this, executor);
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

		Object bean = factoryBean.execute();
		initializationBean(beanName, bean);
		return bean;
	}

	public BeanPostProcessors getBeanPostProcessors() {
		return beanPostProcessors;
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

	public Elements<String> getFactoryBeanNames() {
		return Elements.of(factoryBeanMap.keySet());
	}

	public ParameterExtractors<BeanFactory> getParameterExtractors() {
		return parameterExtractors;
	}

	public BeanFactory getParentBeanFactory() {
		return parentBeanFactory;
	}

	@Override
	public Scope getScope() {
		return scope;
	}

	public ServiceInjectors<Object> getServiceInjectors() {
		return serviceInjectors;
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
						singleton = factoryBean.execute();
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
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		if (!isFactoryBean(name) && super.containsSingleton(name)) {
			Object singleton = super.getSingleton(name);
			return singleton == null ? Object.class : singleton.getClass();
		}

		FactoryBean<? extends Object> factoryBean = getFactoryBean(name);
		if (factoryBean == null) {
			throw new NoSuchBeanDefinitionException(name);
		}

		return factoryBean.getReturnTypeDescriptor().getResolvableType().getRawClass();
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
				((InitializingBean) bean).init();
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

		if (isSingleton(beanName)) {
			Lock writeLock = getReadWriteLock().writeLock();
			writeLock.lock();
			try {
				if (isInitializedSingleton(beanName)) {
					throw new BeansException("Already initialized '" + beanName + "'");
				}

				_initializationBean(beanName, bean);
			} finally {
				writeLock.unlock();
			}
		} else {
			_initializationBean(beanName, bean);
		}
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
		return false;
	}

	private boolean isInitializedSingleton(String beanName) {
		if (initializedSingletonSet.contains(beanName)) {
			return true;
		}

		for (String alias : getAliases(beanName)) {
			if (initializedSingletonSet.contains(alias)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSingleton(String beanName) throws BeansException {
		if (containsSingleton(beanName)) {
			return true;
		}

		FactoryBean<? extends Object> factoryBean = getFactoryBean(beanName);
		if (factoryBean == null) {
			throw new NoSuchBeanDefinitionException(beanName);
		}

		return factoryBean.isSingleton();
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

		return factoryBean.getReturnTypeDescriptor().getResolvableType().isAssignableFrom(typeToMatch);
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

		return factoryBean.getReturnTypeDescriptor().getResolvableType().isAssignableFrom(typeToMatch);
	}

	@Override
	public void registerFactoryBean(String beanName, FactoryBean<? extends Object> factoryBean) throws BeansException {
		if (factoryBeanMap.putIfAbsent(beanName, factoryBean) != null) {
			throw new BeansException("Existing factory beans '" + beanName + "'");
		}
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

	private boolean removeInitializedSingleton(String beanName) {
		if (initializedSingletonSet.remove(beanName)) {
			return true;
		}

		for (String alias : getAliases(beanName)) {
			if (initializedSingletonSet.remove(alias)) {
				return true;
			}
		}
		return false;
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

	public void setParentBeanFactory(BeanFactory parentBeanFactory) {
		if (parentBeanFactory == this) {

		}

		BeanFactory parent = parentBeanFactory;
		while (parent != null) {
			if (parent == this) {
				throw new BeansException("There is a circular dependency between Beanfactory " + this
						+ " and BeanFactory " + parentBeanFactory);
			}

			if (parent instanceof HierarchicalBeanFactory) {
				parent = ((HierarchicalBeanFactory) parent).getParentBeanFactory();
			}
		}
		this.parentBeanFactory = parentBeanFactory;
	}
}
