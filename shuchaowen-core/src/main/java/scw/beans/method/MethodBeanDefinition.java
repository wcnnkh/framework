package scw.beans.method;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;

import scw.beans.AbstractBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.instance.AutoSource;
import scw.core.instance.EnumerationMethodParameterDescriptors;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.lang.NotSupportedException;
import scw.value.property.PropertyFactory;

public class MethodBeanDefinition extends AbstractBeanDefinition {
	private final Method method;
	private final Class<?> methodTargetClass;
	private AutoSource<Method> autoSource;

	public MethodBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> methodTargetClass,
			Method method) {
		super(beanFactory, propertyFactory, method.getReturnType());
		this.methodTargetClass = methodTargetClass;
		this.method = method;
		this.autoSource = new AutoSource<Method>(beanFactory, propertyFactory, getTargetClass(),
				ParameterUtils.getParameterDescriptors(method), method);
	}

	@Override
	public AnnotatedElement getAnnotatedElement() {
		return method;
	}

	@Override
	protected boolean isProxy() {
		return BeanUtils.isAopEnable(method.getReturnType(), method) && method.getReturnType().isInterface();
	}

	public boolean isInstance() {
		return autoSource.isAuto();
	}

	public Object create() throws Exception {
		if (!isInstance()) {
			throw new NotSupportedException("不支持的构造方式");
		}

		Object[] args = autoSource.getAutoArgs();
		return invoke(method, args);
	}

	private Object invoke(Method method, Object[] args) throws Exception {
		ReflectionUtils.makeAccessible(method);
		Object bean = method.invoke(
				Modifier.isStatic(method.getModifiers()) ? null : beanFactory.getInstance(methodTargetClass), args);

		if (isProxy()) {
			return createInstanceProxy(bean, getTargetClass(), null).create();
		}
		return bean;
	}

	public Class<? extends Object> getTargetClass() {
		return method.getReturnType();
	}

	public Object create(Object... params) throws Exception {
		Enumeration<ParameterDescriptor[]> enumeration = enumeration();
		while (enumeration.hasMoreElements()) {
			ParameterDescriptor[] parameterDescriptors = enumeration.nextElement();
			if (ParameterUtils.isAssignableValue(parameterDescriptors, params, true)) {
				Method invoker = methodTargetClass.getDeclaredMethod(method.getName(),
						ParameterUtils.toParameterTypes(parameterDescriptors));
				return invoke(invoker, params);
			}
		}
		throw new NotSupportedException(method.toString());
	}

	public Object create(Class<?>[] parameterTypes, Object... params) throws Exception {
		Method method = methodTargetClass.getDeclaredMethod(this.method.getName(), parameterTypes);
		return invoke(method, params);
	}

	@Override
	public Enumeration<ParameterDescriptor[]> enumeration() {
		return new EnumerationMethodParameterDescriptors(methodTargetClass, method, true);
	}

	public Method getMethod() {
		return method;
	}

	public Class<?> getMethodTargetClass() {
		return methodTargetClass;
	}
}
