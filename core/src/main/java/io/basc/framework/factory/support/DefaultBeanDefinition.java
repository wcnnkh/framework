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
import io.basc.framework.core.parameter.ParameterDescriptor;
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
import io.basc.framework.lang.NotFoundException;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
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
	private Boolean singleton;
	private TypeDescriptor typeDescriptor;
	private Boolean external;

	public DefaultBeanDefinition(TypeDescriptor typeDescriptor) {
		this.typeDescriptor = typeDescriptor;
	}

	private boolean canCreateInterfaceInsance() {
		return typeDescriptor.getType().isInterface() && isAopEnable(getBeanResolver());
	}

	public Aop getAop() {
		return aop;
	}

	public void setAop(Aop aop) {
		this.aop = aop;
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
		Aop aop = getAop();
		if (aop != null && canCreateInterfaceInsance()) {
			return createProxy(aop, typeDescriptor, null).create();
		}

		if (!isInstance()) {
			throw new UnsupportedException(getTypeDescriptor().getName());
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
		BeanResolver beanResolver = getBeanResolver();
		ParameterDescriptors parameterDescriptors = getParameterDescriptors(beanResolver, params);
		if (parameterDescriptors == null) {
			throw new NotFoundException(getTypeDescriptor() + "找不到指定的构造方法");
		}
		return createInternal(beanResolver, typeDescriptor, parameterDescriptors, params);
	}

	protected Proxy createInstanceProxy(Aop aop, Object instance, Class<?> targetClass, Class<?>[] interfaces) {
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
		Aop aop = getAop();
		if (aop != null && isAopEnable(typeDescriptor, beanResolver)) {
			return createProxyInstance(aop, typeDescriptor, parameterDescriptors.getTypes(), params);
		}

		Constructor<?> constructor = ReflectionUtils.getDeclaredConstructor(typeDescriptor.getType(),
				parameterDescriptors.getTypes());
		Object instance = ReflectionUtils.newInstance(constructor, params);
		return instance;
	}

	protected Proxy createProxy(Aop aop, TypeDescriptor typeDescriptor, Class<?>[] interfaces) {
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

	protected Object createProxyInstance(Aop aop, TypeDescriptor typeDescriptor, Class<?>[] parameterTypes,
			Object[] args) {
		if (typeDescriptor.getType().isInterface() && methodInterceptors.isEmpty()) {
			logger.warn("empty filter: {}", typeDescriptor.getName());
		}

		Proxy proxy = createProxy(aop, typeDescriptor, null);
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
		String id = this.id;
		if (StringUtils.isEmpty(id)) {
			BeanResolver beanResolver = getBeanResolver();
			if (beanResolver != null) {
				id = beanResolver.getId(typeDescriptor);
			}
		}
		return StringUtils.isEmpty(id) ? getDefaultId() : id;
	}

	protected String getDefaultId() {
		return typeDescriptor.getType().getName();
	}

	public ConfigurableServices<BeanPostProcessor> getInitProcessors() {
		return initProcessors;
	}

	public ConfigurableMethodInterceptor getMethodInterceptors() {
		return methodInterceptors;
	}

	public Collection<String> getNames() {
		Collection<String> names = this.names;
		if (CollectionUtils.isEmpty(names)) {
			BeanResolver beanResolver = getBeanResolver();
			if (beanResolver != null) {
				names = beanResolver.getNames(typeDescriptor);
			}
		}
		return CollectionUtils.isEmpty(names) ? Collections.emptyList() : names;
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

	protected boolean isAssignableValue(BeanResolver beanResolver, ParameterDescriptors parameterDescriptors,
			Object[] params) {
		// 异或运算，如果两个不同则结果为1
		if (parameterDescriptors.size() == 0 ^ ArrayUtils.isEmpty(params)) {
			return false;
		}

		if (parameterDescriptors.size() != params.length) {
			return false;
		}

		int index = 0;
		for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
			Object value = params[index++];
			if (value == null && beanResolver != null && beanResolver.isNullable(parameterDescriptor)) {
				return false;
			}

			if (!ClassUtils.isAssignableValue(parameterDescriptor.getType(), value)) {
				return false;
			}
		}
		return true;
	}

	protected ParameterDescriptors getParameterDescriptors(BeanResolver beanResolver, Object[] params) {
		for (ParameterDescriptors parameterDescriptors : this) {
			if (isAssignableValue(beanResolver, parameterDescriptors, params)) {
				return parameterDescriptors;
			}
		}
		return null;
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
		return getAop() != null && (!methodInterceptors.isEmpty()
				|| (beanResolver != null && beanResolver.isAopEnable(typeDescriptor)));
	}

	public boolean isInstance() {
		return canCreateInterfaceInsance() || isInstance(isAopEnable(getBeanResolver()));
	}

	public boolean isInstance(boolean supportAbstract) {
		if (!supportAbstract && Modifier.isAbstract(getTypeDescriptor().getType().getModifiers())) {
			return false;
		}
		return getParameterDescriptors(getBeanResolver()) != null;
	}

	public boolean isInstance(Class<?>[] parameterTypes) {
		return getParameterDescriptors(parameterTypes) != null;
	}

	public boolean isInstance(Object... params) {
		return getParameterDescriptors(getBeanResolver(), params) != null;
	}

	public boolean isNew() {
		return isNew;
	}

	@Override
	public boolean isSingleton() {
		BeanResolver beanResolver = getBeanResolver();
		if (this.singleton == null && beanResolver != null) {
			return beanResolver.isSingleton(typeDescriptor);
		}
		return this.singleton == null ? true : singleton;
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

	public void setExternal(Boolean external) {
		this.external = external;
	}

	@Override
	public boolean isExternal() {
		if (external == null && beanResolver != null) {
			return beanResolver.isExternal(typeDescriptor);
		}

		return external == null ? false : external;
	}
}
