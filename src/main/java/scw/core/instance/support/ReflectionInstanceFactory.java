package scw.core.instance.support;

import java.lang.reflect.Constructor;

import scw.core.instance.InstanceFactory;
import scw.core.reflect.ReflectUtils;

public class ReflectionInstanceFactory implements InstanceFactory {

	public <T> T getInstance(Class<T> type) {
		if (type == null) {
			return null;
		}

		Constructor<?> constructor = ReflectUtils.getConstructor(type, false);
		if (constructor == null) {
			return null;
		}

		return newInstance(constructor);
	}

	private Class<?> forName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T newInstance(Constructor<?> constructor, Object... params) {
		if (!constructor.isAccessible()) {
			constructor.setAccessible(true);
		}

		try {
			return (T) constructor.newInstance(params);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		return (T) getInstance(forName(name));
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Object... params) {
		return (T) getInstance(forName(name), params);
	}

	public <T> T getInstance(Class<T> type, Object... params) {
		if (type == null) {
			return null;
		}

		Constructor<?> constructor = ReflectUtils.findConstructorByParameters(type, false, params);
		if (constructor == null) {
			return null;
		}

		return newInstance(constructor, params);
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Class<?>[] parameterTypes, Object... params) {
		return (T) getInstance(forName(name), parameterTypes, params);
	}

	public <T> T getInstance(Class<T> type, Class<?>[] parameterTypes, Object... params) {
		if (type == null) {
			return null;
		}

		Constructor<?> constructor = null;
		try {
			constructor = ReflectUtils.getConstructor(type, false, parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}

		return newInstance(constructor, params);
	}

}
