package io.basc.framework.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import io.basc.framework.aop.Aop;
import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.Proxy;
import io.basc.framework.aop.support.ConfigurableMethodInterceptor;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ExecutableParameterDescriptorsIterator;
import io.basc.framework.core.parameter.ParameterDescriptors;
import io.basc.framework.core.parameter.ParameterUtils;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanPostProcessor;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.BeansException;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.factory.ParametersFactory;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.lang.NotFoundException;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.StringUtils;

public class DefaultBeanDefinition implements BeanDefinition, Cloneable {
	private static Logger logger = LoggerFactory.getLogger(DefaultBeanDefinition.class);
	private Aop aop;
	private final ConfigurableServices<BeanPostProcessor> dependenceProcessors = new ConfigurableServices<BeanPostProcessor>(
			BeanPostProcessor.class);
	private final ConfigurableServices<BeanPostProcessor> destroyProcessors = new ConfigurableServices<BeanPostProcessor>(
			BeanPostProcessor.class);
	private BeanResolver beanResolver;
	private String id;
	private final ConfigurableServices<BeanPostProcessor> initProcessors = new ConfigurableServices<BeanPostProcessor>(
			BeanPostProcessor.class);
	private boolean isNew = true;
	private final ConfigurableMethodInterceptor methodInterceptors = new ConfigurableMethodInterceptor();
	private Collection<String> names;
	private volatile ParameterDescriptors parameterDescriptors;
	@SuppressWarnings("rawtypes")
	private volatile ServiceLoader serviceLoader;
	private ServiceLoaderFactory serviceLoaderFactory;
	private Boolean singleton;
	private TypeDescriptor typeDescriptor;

	public DefaultBeanDefinition(TypeDescriptor typeDescriptor) {
		this.typeDescriptor = typeDescriptor;
	}

	private boolean canCreateInterfaceInsance() {
		return typeDescriptor.getType().isInterface() && isAopEnable(getBeanResolver());
	}

	protected ParameterDescriptors checkParameterDescriptors(ParametersFactory parametersFactory) {
		for (ParameterDescriptors parameterDescriptors : this) {
			if (parameterDescriptors.size() == 0
					|| (parametersFactory != null && parametersFactory.isAccept(parameterDescriptors))) {
				return parameterDescriptors;
			}
		}
		return null;
	}

	@Override
	public BeanDefinition clone() {
		try {
			DefaultBeanDefinition beanDefinition = (DefaultBeanDefinition) super.clone();
			beanDefinition.setNew(false);
			return beanDefinition;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object create() throws InstanceException {
		if (canCreateInterfaceInsance()) {
			return createProxy(typeDescriptor, null).create();
		}

		if (!isInstance()) {
			throw new NotSupportedException(getTypeDescriptor().getName());
		}

		if (serviceLoader != null) {
			for (Object instance : serviceLoader) {
				return instance;
			}
		}

		BeanResolver beanResolver = getBeanResolver();
		ParameterDescriptors parameterDescriptors = getParameterDescriptors(beanResolver);
		return createInternal(beanResolver, typeDescriptor, parameterDescriptors,
				parameterDescriptors.size() == 0 ? new Object[0] : beanResolver.getParameters(parameterDescriptors));
	}

	public Object create(Class<?>[] parameterTypes, Object[] params) throws InstanceException {
		ParameterDescriptors parameterDescriptors = getParameterDescriptors(parameterTypes);
		if (parameterDescriptors == null) {
			throw new NotFoundException(typeDescriptor + "找不到指定的构造方法");
		}

		return createInternal(getBeanResolver(), typeDescriptor, parameterDescriptors, params);
	}

	public Object create(Object... params) throws InstanceException {
		ParameterDescriptors parameterDescriptors = getParameterDescriptors(params);
		if (parameterDescriptors == null) {
			throw new NotFoundException(getTypeDescriptor() + "找不到指定的构造方法");
		}
		return createInternal(getBeanResolver(), typeDescriptor, parameterDescriptors, params);
	}

	protected Proxy createInstanceProxy(Object instance, Class<?> targetClass, Class<?>[] interfaces) {
		Class<?>[] interfacesToUse = interfaces;
		if (ArrayUtils.isEmpty(interfacesToUse)) {
			interfacesToUse = RuntimeBean.PROXY_INTERFACES;
		} else {
			interfacesToUse = ArrayUtils.merge(interfacesToUse, RuntimeBean.PROXY_INTERFACES);
		}

		ConfigurableMethodInterceptor interceptors = new ConfigurableMethodInterceptor();
		interceptors.addMethodInterceptor(new RuntimeBean.RuntimeBeanMethodInterceptor(this));
		interceptors.addMethodInterceptor(methodInterceptors);
		return aop.getProxy(targetClass, instance, interfaces, interceptors);
	}

	protected Object createInternal(BeanResolver beanResolver, TypeDescriptor typeDescriptor,
			ParameterDescriptors parameterDescriptors, Object[] params) {
		if (isAopEnable(typeDescriptor, beanResolver)) {
			return createProxyInstance(typeDescriptor, parameterDescriptors.getTypes(), params);
		}

		Constructor<?> constructor = ReflectionUtils.getDeclaredConstructor(typeDescriptor.getType(),
				parameterDescriptors.getTypes());
		Object instance = ReflectionUtils.newInstance(constructor, params);
		return instance;
	}

	protected Proxy createProxy(TypeDescriptor typeDescriptor, Class<?>[] interfaces) {
		Class<?>[] interfacesToUse = interfaces;
		if (ArrayUtils.isEmpty(interfacesToUse)) {
			interfacesToUse = RuntimeBean.PROXY_INTERFACES;
		} else {
			interfacesToUse = ArrayUtils.merge(interfacesToUse, RuntimeBean.PROXY_INTERFACES);
		}

		MethodInterceptor interceptor = new RuntimeBean.RuntimeBeanMethodInterceptor(this);
		if (methodInterceptors.isEmpty()) {
			return aop.getProxy(typeDescriptor.getType(), interfacesToUse, interceptor);
		}

		ConfigurableMethodInterceptor interceptors = new ConfigurableMethodInterceptor();
		interceptors.addMethodInterceptor(interceptor);
		interceptors.addMethodInterceptor(getMethodInterceptors());
		return aop.getProxy(typeDescriptor.getType(), interfacesToUse, interceptors);
	}

	protected Object createProxyInstance(TypeDescriptor typeDescriptor, Class<?>[] parameterTypes, Object[] args) {
		if (typeDescriptor.getType().isInterface() && methodInterceptors.isEmpty()) {
			logger.warn("empty filter: {}", typeDescriptor.getName());
		}

		Proxy proxy = createProxy(typeDescriptor, null);
		return proxy.create(parameterTypes, args);
	}

	@Override
	public void dependence(Object instance) throws BeansException {
		for (BeanPostProcessor processor : dependenceProcessors) {
			processor.processPostBean(instance, this);
		}
	}

	@Override
	public void destroy(Object instance) throws BeansException {
		for (BeanPostProcessor processor : destroyProcessors) {
			processor.processPostBean(instance, this);
		}
	}

	public ConfigurableServices<BeanPostProcessor> getDependenceProcessors() {
		return dependenceProcessors;
	}

	public ConfigurableServices<BeanPostProcessor> getDestroyProcessors() {
		return destroyProcessors;
	}

	public BeanResolver getBeanResolver() {
		return beanResolver;
	}

	public String getId() {
		BeanResolver beanResolver = getBeanResolver();
		if (StringUtils.isEmpty(this.id) && beanResolver != null) {
			return beanResolver.getId(typeDescriptor);
		}
		return StringUtils.isEmpty(this.id) ? typeDescriptor.getType().getName() : this.id;
	}

	public ConfigurableServices<BeanPostProcessor> getInitProcessors() {
		return initProcessors;
	}

	public ConfigurableMethodInterceptor getMethodInterceptors() {
		return methodInterceptors;
	}

	public Collection<String> getNames() {
		BeanResolver beanResolver = getBeanResolver();
		if (this.names == null && beanResolver != null) {
			return beanResolver.getNames(typeDescriptor);
		}
		return this.names == null ? Collections.emptyList() : this.names;
	}

	public ParameterDescriptors getParameterDescriptors(ParametersFactory parametersFactory) {
		if (parameterDescriptors == null) {
			synchronized (this) {
				if (parameterDescriptors == null) {
					ParameterDescriptors parameterDescriptors = checkParameterDescriptors(parametersFactory);
					if (parameterDescriptors != null) {
						this.parameterDescriptors = parameterDescriptors;
					}
				}
			}
		}
		return parameterDescriptors;
	}

	protected ParameterDescriptors getParameterDescriptors(Class<?>[] parameterTypes) {
		for (ParameterDescriptors parameterDescriptors : this) {
			if (ParameterUtils.isisAssignable(parameterDescriptors, parameterTypes)) {
				return parameterDescriptors;
			}
		}
		return null;
	}

	protected ParameterDescriptors getParameterDescriptors(Object[] params) {
		for (ParameterDescriptors parameterDescriptors : this) {
			if (ParameterUtils.isAssignableValue(parameterDescriptors, params)) {
				return parameterDescriptors;
			}
		}
		return null;
	}

	protected <S> ServiceLoader<S> getServiceLoader(Class<S> clazz) {
		ServiceLoaderFactory serviceLoaderFactory = getServiceLoaderFactory();
		if (serviceLoaderFactory == null) {
			return null;
		}

		return serviceLoaderFactory.getServiceLoader(clazz);
	}

	public Boolean getSingleton() {
		return singleton;
	}

	protected String getStringDescribe() {
		return ReflectionUtils.toString(this);
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	@Override
	public void init(Object instance) throws BeansException {
		for (BeanPostProcessor processor : initProcessors) {
			processor.processPostBean(instance, this);
		}
	}

	public boolean isAopEnable(TypeDescriptor typeDescriptor) {
		return isAopEnable(typeDescriptor, beanResolver);
	}

	public boolean isAopEnable(BeanResolver beanResolver) {
		return isAopEnable(typeDescriptor, beanResolver);
	}

	public boolean isAopEnable(TypeDescriptor typeDescriptor, BeanResolver beanResolver) {
		return !methodInterceptors.isEmpty() || (beanResolver != null && beanResolver.isAopEnable(typeDescriptor));
	}

	public boolean isInstance() {
		return canCreateInterfaceInsance() || isInstance(isAopEnable(getBeanResolver()));
	}

	public boolean isInstance(boolean supportAbstract) {
		if (serviceLoader == null) {
			serviceLoader = getServiceLoader(getTypeDescriptor().getType());
		}

		if (serviceLoader.iterator().hasNext()) {
			return true;
		}

		if (!supportAbstract && Modifier.isAbstract(getTypeDescriptor().getType().getModifiers())) {
			return false;
		}
		return getParameterDescriptors(getBeanResolver()) != null;
	}

	public boolean isInstance(Class<?>[] parameterTypes) {
		return getParameterDescriptors(parameterTypes) != null;
	}

	public boolean isInstance(Object... params) {
		return getParameterDescriptors(params) != null;
	}

	public boolean isNew() {
		return isNew;
	}

	@Override
	public boolean isSingleton() {
		BeanResolver beanResolver = getBeanResolver();
		if (this.singleton != null && beanResolver != null) {
			return beanResolver.isSingleton(typeDescriptor);
		}
		return this.singleton == null ? false : singleton;
	}

	public Iterator<ParameterDescriptors> iterator() {
		return new ExecutableParameterDescriptorsIterator(getTypeDescriptor().getType());
	}

	public void setBeanResolver(BeanResolver beanResolver) {
		this.beanResolver = beanResolver;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNames(Collection<String> names) {
		this.names = names;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public void setSingleton(Boolean singleton) {
		this.singleton = singleton;
	}

	/**
	 * 如果要重写请重写 {@link #getStringDescribe()}
	 */
	@Override
	public final String toString() {
		return getClass().getName() + "[" + getStringDescribe() + "]";
	}

	public ServiceLoaderFactory getServiceLoaderFactory() {
		return serviceLoaderFactory;
	}

	public void setServiceLoaderFactory(ServiceLoaderFactory serviceLoaderFactory) {
		this.serviceLoaderFactory = serviceLoaderFactory;
	}
}
