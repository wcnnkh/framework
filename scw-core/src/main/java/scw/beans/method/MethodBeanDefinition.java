package scw.beans.method;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import scw.beans.BeanFactory;
import scw.beans.DefaultBeanDefinition;
import scw.core.parameter.MethodParameterDescriptors;
import scw.core.parameter.MethodParameterDescriptorsIterator;
import scw.core.parameter.ParameterDescriptors;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.lang.NotSupportedException;
import scw.value.property.PropertyFactory;

public class MethodBeanDefinition extends DefaultBeanDefinition {
	private final Method method;
	private final Class<?> methodTargetClass;
	private final MethodParameterDescriptors methodParameterDescriptors;
	
	public MethodBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> methodTargetClass,
			Method method) {
		super(beanFactory, propertyFactory, method.getReturnType());
		this.methodTargetClass = methodTargetClass;
		this.method = method;
		this.methodParameterDescriptors = new MethodParameterDescriptors(methodTargetClass, method);
	}

	@Override
	public AnnotatedElement getAnnotatedElement() {
		return method;
	}

	public boolean isInstance() {
		return isAccept(methodParameterDescriptors);
	}

	public Object create() throws Exception {
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
	
	private Object invoke(Method method, Object[] args) throws Exception {
		ReflectionUtils.makeAccessible(method);
		Object bean = method.invoke(
				Modifier.isStatic(method.getModifiers()) ? null : beanFactory.getInstance(methodTargetClass), args);
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

	public Class<? extends Object> getTargetClass() {
		return method.getReturnType();
	}

	public Object create(Object... params) throws Exception {
		for (ParameterDescriptors parameterDescriptors : this) {
			if (ParameterUtils.isAssignableValue(parameterDescriptors, params, true)) {
				return create(parameterDescriptors.getTypes(), params);
			}
		}
		throw new NotSupportedException(method.toString());
	}

	public Object create(Class<?>[] parameterTypes, Object... params) throws Exception {
		Method method = methodTargetClass.getDeclaredMethod(this.method.getName(), parameterTypes);
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
