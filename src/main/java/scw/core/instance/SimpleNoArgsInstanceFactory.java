package scw.core.instance;

import java.lang.reflect.Constructor;

import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;

public class SimpleNoArgsInstanceFactory implements NoArgsInstanceFactory {

	protected Class<?> forName(String name) {
		try {
			return ClassUtils.forName(name);
		} catch (ClassNotFoundException e) {
		}
		return null;
	}

	protected <T> Constructor<T> getConstructor(Class<T> clazz) {
		Constructor<T> constructor = null;
		try {
			constructor = clazz.getConstructor();
			ReflectionUtils.setAccessibleConstructor(constructor);
		} catch (NoSuchMethodException e) {
		}
		return constructor;
	}

	public boolean isSingleton(String name) {
		return false;
	}

	public boolean isSingleton(Class<?> clazz) {
		return false;
	}

	public <T> T getInstance(Class<T> clazz) {
		Constructor<T> constructor = getConstructor(clazz);
		if (constructor == null) {
			throw new CannotInstantiateException(clazz.getName());
		}

		try {
			return constructor.newInstance();
		} catch (Exception e) {
			throw new InstanceException(clazz.getName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		return (T) getInstance(forName(name));
	}

	public boolean isInstance(String name) {
		return isInstance(forName(name));
	}

	public boolean isInstance(Class<?> clazz) {
		return getConstructor(clazz) != null;
	}

}
