package scw.beans.method;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import scw.beans.AbstractBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.BeansException;
import scw.beans.annotation.Bean;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.lang.NotSupportException;
import scw.util.Value;
import scw.util.ValueFactory;

@SuppressWarnings("unchecked")
public class MethodBeanDefinition extends AbstractBeanDefinition {
	private Class<?> targetClazz;
	private Method method;
	private ValueFactory<String, ? extends Value> valueFactory;

	public MethodBeanDefinition(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> clazz, Class<?> targetClazz, Method method,
			ValueFactory<String, ? extends Value> valueFactory) {
		super(valueWiredManager, beanFactory, propertyFactory, clazz);
		this.targetClazz = targetClazz;
		this.method = method;
		this.valueFactory = valueFactory;
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
		return InstanceUtils.isAuto(beanFactory, propertyFactory, valueFactory, getType(),
				ParameterUtils.getParameterConfigs(method), method);
	}

	public <T> T create() {
		if (!isInstance()) {
			throw new NotSupportException("不支持的构造方式");
		}

		Object[] args = InstanceUtils.getAutoArgs(beanFactory, propertyFactory, valueFactory, getType(),
				ParameterUtils.getParameterConfigs(method));
		return (T) invoke(method, args);
	}

	private Object invoke(Method method, Object[] args) {
		Object bean;
		try {
			ReflectionUtils.setAccessibleMethod(method);
			bean = method.invoke(Modifier.isStatic(method.getModifiers())? null:beanFactory.getInstance(targetClazz), args);
		} catch (Exception e) {
			throw new BeansException(getType() + "", e);
		}

		if (isProxy()) {
			bean = BeanUtils.createProxy(beanFactory, getType(), bean, null, null).create();
		}
		return bean;
	}

	public <T> T create(Object... params) {
		for (Method method : targetClazz.getDeclaredMethods()) {
			if (!method.getName().equals(this.method.getName())) {
				continue;
			}

			if (ClassUtils.isAssignableValue(Arrays.asList(method.getParameterTypes()), Arrays.asList(params))) {
				return (T) invoke(method, params);
			}
		}
		throw new NotSupportException(method.toString());
	}

	public <T> T create(Class<?>[] parameterTypes, Object... params) {
		Method method;
		try {
			method = targetClazz.getDeclaredMethod(this.method.getName(), parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new NotSupportException(e);
		}
		return (T) invoke(method, params);
	}

}
