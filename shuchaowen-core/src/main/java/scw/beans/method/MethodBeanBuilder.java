package scw.beans.method;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.builder.AbstractBeanBuilder;
import scw.core.instance.AutoSource;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.lang.NotSupportedException;
import scw.util.value.property.PropertyFactory;

public class MethodBeanBuilder extends AbstractBeanBuilder {
	private Method method;
	private Class<?> methodTargetClass;
	private AutoSource<Method> autoSource;

	public MethodBeanBuilder(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> methodTargetClass,
			Method method) {
		super(beanFactory, propertyFactory, method.getReturnType());
		this.methodTargetClass = methodTargetClass;
		this.method = method;
		this.autoSource = new AutoSource<Method>(beanFactory, propertyFactory, getTargetClass(),
				ParameterUtils.getParameterDescriptors(method), method);
	}

	@Override
	protected boolean isProxy() {
		return BeanUtils.isProxy(method.getReturnType(), method) && method.getReturnType().isInterface();
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
		ReflectionUtils.setAccessibleMethod(method);
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
