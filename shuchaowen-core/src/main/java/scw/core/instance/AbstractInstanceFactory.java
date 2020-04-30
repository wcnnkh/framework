package scw.core.instance;

public abstract class AbstractInstanceFactory implements InstanceFactory {

	protected <T> InstanceBuilder<T> getInstanceBuilder(Class<? extends T> clazz) {
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

	public boolean isInstance(String name) {
		InstanceBuilder<?> instanceBuilder = getInstanceBuilder(name);
		if (instanceBuilder == null) {
			return false;
		}

		return instanceBuilder.isInstance();
	}

	public boolean isInstance(Class<?> clazz) {
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
