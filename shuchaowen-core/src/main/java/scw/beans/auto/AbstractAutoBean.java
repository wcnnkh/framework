package scw.beans.auto;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
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
		this.proxy = BeanUtils.isProxy(type, getAnnotatedElement());
	}

	protected abstract Collection<String> getFilterNames();
	
	public String getId() {
		return type.getName();
	}

	protected boolean isProxy() {
		return proxy;
	}
	
	public boolean isSingleton() {
		return true;
	}

	public boolean isReference() {
		return false;
	}

	public Class<?> getTargetClass() {
		return type;
	}

	public Object create(Object... params) throws Exception{
		Constructor<?> constructor = ReflectionUtils
				.findConstructorByParameters(type, false, params);
		if (constructor == null) {
			throw new NotFoundException(type + "找不到指定的构造方法");
		}

		if (isProxy()) {
			return BeanUtils.createProxy(beanFactory, type,
					getFilterNames(), null).create(
					constructor.getParameterTypes(), params);
		} else {
			return constructor.newInstance(params);
		}
	}

	public Object create(Class<?>[] parameterTypes, Object... params)
			throws Exception {
		Constructor<?> constructor = ReflectionUtils.getConstructor(type,
				false, parameterTypes);
		if (constructor == null) {
			throw new NotFoundException(type + "找不到指定的构造方法");
		}

		if (isProxy()) {
			return BeanUtils.createProxy(beanFactory, type,
					getFilterNames(), null).create(
					constructor.getParameterTypes(), params);
		} else {
			return constructor.newInstance(ClassUtils.cast(
					constructor.getParameterTypes(), params));
		}
	}
	
	public AnnotatedElement getAnnotatedElement() {
		return getTargetClass();
	}
	
	public void init(Object instance) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
