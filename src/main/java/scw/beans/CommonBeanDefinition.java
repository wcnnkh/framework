package scw.beans;

import java.lang.reflect.Constructor;

import scw.beans.annotation.Proxy;
import scw.beans.auto.AutoBean;
import scw.beans.auto.SimpleAutoBean;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.aop.Filter;
import scw.core.cglib.proxy.Enhancer;
import scw.core.exception.BeansException;
import scw.core.exception.NotFoundException;
import scw.core.exception.NotSupportException;
import scw.core.reflect.ReflectUtils;

public final class CommonBeanDefinition extends AbstractBeanDefinition {
	private AutoBean autoBean;
	private boolean instance;

	public CommonBeanDefinition(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> type) {
		super(valueWiredManager, beanFactory, propertyFactory, type);
		init();

		if (getType().isInterface()) {
			Proxy proxy = getType().getAnnotation(Proxy.class);
			this.instance = proxy != null;
		} else {
			this.autoBean = new SimpleAutoBean(beanFactory, type, propertyFactory);
			this.instance = autoBean != null && autoBean.isInstance();
		}
	}

	public boolean isInstance() {
		return instance;
	}

	protected Enhancer getProxyEnhancer() {
		Proxy proxy = getType().getAnnotation(Proxy.class);
		return BeanUtils.createEnhancer(getType(), beanFactory,
				proxy == null ? null : (Filter) beanFactory.getInstance(proxy.value()));
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		if (!isInstance()) {
			throw new NotSupportException(getType().toString());
		}

		if (getType().isInterface()) {
			Proxy proxy = getType().getAnnotation(Proxy.class);
			Filter filter = beanFactory.getInstance(proxy.value());
			return (T) BeanUtils.proxyInterface(beanFactory, getType(), filter);
		}

		try {
			return (T) autoBean.create();
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