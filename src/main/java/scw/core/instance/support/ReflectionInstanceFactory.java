package scw.core.instance.support;

import java.lang.reflect.Constructor;

import scw.core.instance.InstanceException;
import scw.core.instance.InstanceFactory;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;

@SuppressWarnings("unchecked")
public class ReflectionInstanceFactory implements InstanceFactory {
	public <T> T getInstance(Class<T> type) {
		if (type == null) {
			return null;
		}

		Constructor<?> constructor = ReflectionUtils.getConstructor(type, false);
		if (constructor == null) {
			return null;
		}

		return newInstance(constructor);
	}

	public static Class<?> forName(String className) {
		if (StringUtils.isEmpty(className)) {
			return null;
		}

		try {
			return ClassUtils.forName(className);
		} catch (Throwable e) {
			if (e instanceof InstanceException) {
				throw (InstanceException) e;
			}
		}
		return null;
	}

	private <T> T newInstance(Constructor<?> constructor, Object... params) {
		if (!constructor.isAccessible()) {
			constructor.setAccessible(true);
		}

		try {
			return (T) constructor.newInstance(params);
		} catch (Throwable e) {
			throw new InstanceException(e);
		}
	}

	public <T> T getInstance(String name) {
		return (T) getInstance(forName(name));
	}

	public <T> T getInstance(String name, Object... params) {
		return (T) getInstance(forName(name), params);
	}

	public <T> T getInstance(Class<T> type, Object... params) {
		if (type == null) {
			return null;
		}

		Constructor<?> constructor = ReflectionUtils.findConstructorByParameters(type, false, params);
		if (constructor == null) {
			return null;
		}

		return newInstance(constructor, params);
	}

	public <T> T getInstance(String name, Class<?>[] parameterTypes, Object... params) {
		return (T) getInstance(forName(name), parameterTypes, params);
	}

	public <T> T getInstance(Class<T> type, Class<?>[] parameterTypes, Object... params) {
		if (type == null) {
			return null;
		}

		Constructor<?> constructor = ReflectionUtils.getConstructor(type, false, parameterTypes);
		if (constructor == null) {
			return null;
		}
		return newInstance(constructor, params);
	}

	public boolean isInstance(String name) {
		if (name == null) {
			return false;
		}

		Class<?> clazz = forName(name);
		return clazz == null ? false : isInstance(clazz);
	}

	public boolean isInstance(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}

		Constructor<?> constructor = ReflectionUtils.getConstructor(clazz, false);
		return constructor != null;
	}

	public boolean isSingleton(String name) {
		return false;
	}

	public boolean isSingleton(Class<?> clazz) {
		return false;
	}

}
