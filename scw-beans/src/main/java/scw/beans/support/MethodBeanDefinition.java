package scw.beans.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.core.parameter.MethodParameterDescriptors;
import scw.core.parameter.MethodParameterDescriptorsIterator;
import scw.core.parameter.ParameterDescriptors;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.lang.NotSupportedException;

public class MethodBeanDefinition extends DefaultBeanDefinition {
	private final Method method;
	private final Class<?> methodTargetClass;
	private final MethodParameterDescriptors methodParameterDescriptors;
	
	public MethodBeanDefinition(BeanFactory beanFactory, Class<?> methodTargetClass,
			Method method) {
		super(beanFactory, method.getReturnType());
		this.methodTargetClass = methodTargetClass;
		this.method = method;
		this.methodParameterDescriptors = new MethodParameterDescriptors(methodTargetClass, method);
	}

	@Override
	public AnnotatedElement getAnnotatedElement() {
		return method;
	}

	private final AtomicBoolean error = new AtomicBoolean();
	public boolean isInstance() {
		boolean accept = isAccept(methodParameterDescriptors);
		if(!accept){
			if(!error.get() && error.compareAndSet(false, true)){
				logger.error("not found {} accept method {}", this, method);
			}
		}
		return accept;
	}

	public Object create() throws BeansException {
		if (!isInstance()) {
			throw new NotSupportedException("不支持的构造方式");
		}

		Object[] args = getParameters(methodParameterDescriptors);
		return invoke(method, args);
	}
	
	@Override
	public boolean isAopEnable() {
		//必须要是接口，因为非接口不一定是无法保证一定可以代理实例
		return getTargetClass().isInterface() && super.isAopEnable();
	}
	
	private Object invoke(Method method, Object[] args) throws BeansException {
		ReflectionUtils.makeAccessible(method);
		Object bean = ReflectionUtils.invokeMethod(method, Modifier.isStatic(method.getModifiers()) ? null : beanFactory.getInstance(methodTargetClass), args);
		if(beanFactory.getAop().isProxy(bean)){
			//已经被代理过的
			return bean;
		}
		
		//必须要是接口，因为非接口不一定是无法保证一定可以代理实例
		if (method.getReturnType().isInterface() && (isAopEnable(method.getReturnType(), method) || isAopEnable(bean.getClass(), method))) {
			return createInstanceProxy(bean, getTargetClass(), null).create();
		}
		return bean;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getTargetClass() {
		return method.getReturnType();
	}

	public Object create(Object... params) throws BeansException {
		for (ParameterDescriptors parameterDescriptors : this) {
			if (ParameterUtils.isAssignableValue(parameterDescriptors, params, true)) {
				return create(parameterDescriptors.getTypes(), params);
			}
		}
		throw new NotSupportedException(method.toString());
	}

	public Object create(Class<?>[] parameterTypes, Object[] params) throws BeansException {
		Method method;
		try {
			method = methodTargetClass.getDeclaredMethod(this.method.getName(), parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new BeansException(e);
		} catch (SecurityException e) {
			throw new BeansException(e);
		}
		return invoke(method, params);
	}

	@Override
	public Iterator<ParameterDescriptors> iterator() {
		return new MethodParameterDescriptorsIterator(methodTargetClass, method, true);
	}

	public Method getMethod() {
		return method;
	}

	public Class<?> getMethodTargetClass() {
		return methodTargetClass;
	}
}
