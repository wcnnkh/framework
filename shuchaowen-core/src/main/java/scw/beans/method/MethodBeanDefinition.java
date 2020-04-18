package scw.beans.method;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import scw.beans.AbstractBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.BeansException;
import scw.beans.annotation.Bean;
import scw.core.annotation.MultiAnnotatedElement;
import scw.core.instance.InstanceUtils;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.lang.UnsupportedException;
import scw.util.value.property.PropertyFactory;

@SuppressWarnings("unchecked")
public class MethodBeanDefinition extends AbstractBeanDefinition {
	private Class<?> methodTargetClass;
	private Method method;

	public MethodBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> returnClass,
			Class<?> methodTargetClass, Method method) {
		super(beanFactory, propertyFactory, returnClass);
		this.methodTargetClass = methodTargetClass;
		this.method = method;
		init();
		Bean bean = method.getAnnotation(Bean.class);
		if (bean != null) {
			this.proxy = bean.proxy();
			this.singleton = bean.singleton();
		}
	}

	public String[] getNames() {
		return null;
	}

	public boolean isInstance() {
		return InstanceUtils.isAuto(beanFactory, propertyFactory,
				getTargetClass(),
				ParameterUtils.getParameterDescriptors(method), null, method);
	}

	public <T> T create() {
		if (!isInstance()) {
			throw new UnsupportedException("不支持的构造方式");
		}

		Object[] args = InstanceUtils.getAutoArgs(beanFactory, propertyFactory,
				getTargetClass(),
				ParameterUtils.getParameterDescriptors(method), null);
		return (T) invoke(method, args);
	}

	private Object invoke(Method method, Object[] args) {
		Object bean;
		try {
			ReflectionUtils.setAccessibleMethod(method);
			bean = method.invoke(
					Modifier.isStatic(method.getModifiers()) ? null
							: beanFactory.getInstance(methodTargetClass), args);
		} catch (Exception e) {
			throw new BeansException(getTargetClass() + "", e);
		}

		if (isProxy()) {
			bean = BeanUtils.createProxy(beanFactory, getTargetClass(), bean,
					null, null).create();
		}
		return bean;
	}

	public <T> T create(Object... params) {
		for (Method method : methodTargetClass.getDeclaredMethods()) {
			if (!method.getName().equals(this.method.getName())) {
				continue;
			}

			if (ClassUtils.isAssignableValue(
					Arrays.asList(method.getParameterTypes()),
					Arrays.asList(params))) {
				return (T) invoke(method, params);
			}
		}
		throw new UnsupportedException(method.toString());
	}

	public <T> T create(Class<?>[] parameterTypes, Object... params) {
		Method method;
		try {
			method = methodTargetClass.getDeclaredMethod(this.method.getName(),
					parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new UnsupportedException(e);
		}
		return (T) invoke(method, params);
	}

	public AnnotatedElement getAnnotatedElement() {
		return new MultiAnnotatedElement(method, super.getAnnotatedElement());
	}

}
