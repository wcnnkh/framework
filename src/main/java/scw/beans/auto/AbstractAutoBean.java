package scw.beans.auto;

import java.lang.reflect.Constructor;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.BeansException;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.lang.NotFoundException;

public abstract class AbstractAutoBean implements AutoBean {
	protected final BeanFactory beanFactory;
	protected final Class<?> type;
	private final boolean proxy;

	public AbstractAutoBean(BeanFactory beanFactory, Class<?> type) {
		this.beanFactory = beanFactory;
		this.type = type;
		this.proxy = BeanUtils.checkProxy(type);
	}

	protected abstract Collection<String> getFilterNames();

	protected boolean isProxy() {
		return proxy;
	}

	public boolean isReference() {
		return false;
	}

	public Class<?> getTargetClass() {
		return type;
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Object... params) {
		Constructor<?> constructor = ReflectionUtils.findConstructorByParameters(type, false, params);
		if (constructor == null) {
			throw new NotFoundException(type + "找不到指定的构造方法");
		}

		if (isProxy()) {
			return (T) BeanUtils.createProxy(beanFactory, type, getFilterNames(), null)
					.create(constructor.getParameterTypes(), params);
		} else {
			try {
				return (T) constructor.newInstance(params);
			} catch (Exception e) {
				throw new BeansException(type.getName(), e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<?>[] parameterTypes, Object... params) {
		Constructor<?> constructor = ReflectionUtils.getConstructor(type, false, parameterTypes);
		if (constructor == null) {
			throw new NotFoundException(type + "找不到指定的构造方法");
		}

		if (isProxy()) {
			return (T) BeanUtils.createProxy(beanFactory, type, getFilterNames(), null)
					.create(constructor.getParameterTypes(), params);
		} else {
			try {
				return (T) constructor.newInstance(ClassUtils.cast(constructor.getParameterTypes(), params));
			} catch (Exception e) {
				throw new BeansException(type.getName(), e);
			}
		}
	}
}
