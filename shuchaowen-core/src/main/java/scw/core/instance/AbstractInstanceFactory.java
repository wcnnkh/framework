package scw.core.instance;

import scw.core.annotation.AnnotationUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;

public abstract class AbstractInstanceFactory implements InstanceFactory {

	protected <T> InstanceBuilder<T> getInstanceBuilder(Class<? extends T> clazz) {
		if(isIgnoreClass(clazz)){
			return null;
		}
		return getInstanceBuilder(clazz.getName());
	}

	protected abstract <T> InstanceBuilder<T> getInstanceBuilder(String name);

	public <T> T getInstance(Class<? extends T> clazz) {
		return getInstance(clazz.getName());
	}

	public <T> T getInstance(String name) {
		InstanceBuilder<T> instanceBuilder = getInstanceBuilder(name);
		if (instanceBuilder == null) {
			return null;
		}

		try {
			return instanceBuilder.create();
		} catch (Exception e) {
			throw new InstanceException(name, e);
		}
	}

	protected boolean isIgnoreClass(Class<?> clazz) {
		return ClassUtils.isPrimitiveOrWrapper(clazz) || AnnotationUtils.isIgnore(clazz) || !ReflectionUtils.isPresent(clazz);
	}

	public boolean isInstance(String name) {
		InstanceBuilder<?> instanceBuilder = getInstanceBuilder(name);
		if (instanceBuilder == null) {
			return false;
		}

		return instanceBuilder.isInstance();
	}

	public boolean isInstance(Class<?> clazz) {
		if (isIgnoreClass(clazz)) {
			return false;
		}

		return isInstance(clazz.getName());
	}

	public <T> T getInstance(String name, Object... params) {
		InstanceBuilder<T> instanceBuilder = getInstanceBuilder(name);
		if (instanceBuilder == null) {
			return null;
		}

		try {
			return instanceBuilder.create(params);
		} catch (Exception e) {
			throw new InstanceException(name, e);
		}
	}

	public <T> T getInstance(Class<? extends T> type, Object... params) {
		return getInstance(type.getName(), params);
	}

	public <T> T getInstance(String name, Class<?>[] parameterTypes, Object... params) {
		InstanceBuilder<T> instanceBuilder = getInstanceBuilder(name);
		if (instanceBuilder == null) {
			return null;
		}

		try {
			return instanceBuilder.create(parameterTypes, params);
		} catch (Exception e) {
			throw new InstanceException(name, e);
		}
	}

	public <T> T getInstance(Class<? extends T> type, Class<?>[] parameterTypes, Object... params) {
		return getInstance(type.getName(), parameterTypes, params);
	}

	public boolean isSingleton(Class<?> clazz) {
		return false;
	}

	public boolean isSingleton(String name) {
		return false;
	}
}
