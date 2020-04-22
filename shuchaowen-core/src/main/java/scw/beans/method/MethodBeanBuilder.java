package scw.beans.method;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import scw.beans.AbstractBeanBuilder;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.instance.InstanceUtils;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.lang.NotSupportedException;
import scw.util.value.property.PropertyFactory;

public class MethodBeanBuilder extends AbstractBeanBuilder {
	private Method method;
	private Class<?> methodTargetClass;

	public MethodBeanBuilder(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> methodTargetClass,
			Method method) {
		super(beanFactory, propertyFactory, method.getReturnType());
		this.methodTargetClass = methodTargetClass;
		this.method = method;
	}

	@Override
	protected boolean isProxy() {
		return BeanUtils.isProxy(method.getReturnType(), method);
	}

	public boolean isInstance() {
		return InstanceUtils.isAuto(beanFactory, propertyFactory, getTargetClass(),
				ParameterUtils.getParameterDescriptors(method), null, method);
	}

	public Object create() throws Exception {
		if (!isInstance()) {
			throw new NotSupportedException("不支持的构造方式");
		}

		Object[] args = InstanceUtils.getAutoArgs(beanFactory, propertyFactory, getTargetClass(),
				ParameterUtils.getParameterDescriptors(method), null);
		return invoke(method, args);
	}

	private Object invoke(Method method, Object[] args) throws Exception {
		ReflectionUtils.setAccessibleMethod(method);
		Object bean = method.invoke(
				Modifier.isStatic(method.getModifiers()) ? null : beanFactory.getInstance(methodTargetClass), args);

		if (isProxy()) {
			return createInstanceProxy(bean, getTargetClass(), null).create(method.getParameterTypes(), args);
		}
		return bean;
	}

	public Class<? extends Object> getTargetClass() {
		return method.getReturnType();
	}

	public Object create(Object... params) throws Exception {
		for (Method method : methodTargetClass.getDeclaredMethods()) {
			if (!method.getName().equals(this.method.getName())) {
				continue;
			}

			if (ClassUtils.isAssignableValue(Arrays.asList(method.getParameterTypes()), Arrays.asList(params))) {
				return invoke(method, params);
			}
		}
		throw new NotSupportedException(method.toString());
	}

	public Object create(Class<?>[] parameterTypes, Object... params) throws Exception {
		Method method = methodTargetClass.getDeclaredMethod(this.method.getName(), parameterTypes);
		return invoke(method, params);
	}
}
