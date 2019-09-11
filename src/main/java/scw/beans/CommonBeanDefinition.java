package scw.beans;

import java.lang.reflect.Constructor;

import scw.beans.annotation.Proxy;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.aop.Filter;
import scw.core.cglib.proxy.Enhancer;
import scw.core.exception.BeansException;
import scw.core.exception.NotFoundException;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectUtils;

public class CommonBeanDefinition extends AbstractBeanDefinition {

	public CommonBeanDefinition(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> type, String[] filterNames) {
		super(valueWiredManager, beanFactory, propertyFactory, type, filterNames);
	}

	protected Enhancer getProxyEnhancer() {
		Proxy proxy = getType().getAnnotation(Proxy.class);
		return BeanUtils.createEnhancer(getType(), beanFactory, filterNames,
				proxy == null ? null : (Filter) beanFactory.getInstance(proxy.value()));
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		Object bean;
		try {
			if (isProxy()) {
				Enhancer enhancer = getProxyEnhancer();
				bean = enhancer.create();
			} else {
				bean = InstanceUtils.getInstance(getType());
			}
			return (T) bean;
		} catch (Exception e) {
			throw new BeansException(getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Object... params) {
		Constructor<T> constructor = (Constructor<T>) ReflectUtils.findConstructorByParameters(getType(), true, params);
		if (constructor == null) {
			throw new NotFoundException(getId() + "找不到指定的构造方法");
		}

		Object bean;
		try {
			if (isProxy()) {
				Enhancer enhancer = getProxyEnhancer();
				bean = enhancer.create(constructor.getParameterTypes(), params);
			} else {
				bean = constructor.newInstance(params);
			}
			return (T) bean;
		} catch (Throwable e) {
			throw new BeansException(getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<?>[] parameterTypes, Object... params) {
		Constructor<?> constructor = ReflectUtils.getConstructor(getType(), false, parameterTypes);
		if (constructor == null) {
			throw new NotFoundException(getId() + "找不到指定的构造方法");
		}

		Object bean;
		try {
			if (isProxy()) {
				Enhancer enhancer = getProxyEnhancer();
				bean = enhancer.create(constructor.getParameterTypes(), params);
			} else {
				bean = constructor.newInstance(params);
			}
			return (T) bean;
		} catch (Throwable e) {
			throw new BeansException(getId(), e);
		}
	}
}