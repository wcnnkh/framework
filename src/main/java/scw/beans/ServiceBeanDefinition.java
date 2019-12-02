package scw.beans;

import java.lang.reflect.Constructor;

import scw.beans.auto.AutoBean;
import scw.beans.auto.SimpleAutoBean;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.cglib.proxy.Enhancer;
import scw.core.exception.BeansException;
import scw.core.exception.NotFoundException;
import scw.core.exception.NotSupportException;
import scw.core.reflect.ReflectionUtils;

public final class ServiceBeanDefinition extends AbstractBeanDefinition {
	private AutoBean autoBean;
	private boolean instance = true;
	private String[] names;

	public ServiceBeanDefinition(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> type) {
		super(valueWiredManager, beanFactory, propertyFactory, type);
		init();
		if (type.isInterface()) {
			this.instance = true;
		} else {
			this.autoBean = new SimpleAutoBean(beanFactory, type, propertyFactory);
			this.instance = autoBean.isInstance();
		}
		this.names = BeanUtils.getServiceNames(getType());
	}

	public boolean isInstance() {
		return instance;
	}

	protected Enhancer getProxyEnhancer() {
		return BeanUtils.createEnhancer(getType(), beanFactory, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		if (!isInstance()) {
			throw new NotSupportException(getType().toString());
		}

		if (getType().isInterface()) {
			return (T) BeanUtils.proxyInterface(beanFactory, getType(), null, null);
		}

		try {
			return (T) autoBean.create();
		} catch (Exception e) {
			throw new BeansException(getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Object... params) {
		Constructor<T> constructor = (Constructor<T>) ReflectionUtils.findConstructorByParameters(getType(), true, params);
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
		Constructor<?> constructor = ReflectionUtils.getConstructor(getType(), false, parameterTypes);
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

	public String[] getNames() {
		return names;
	}
}