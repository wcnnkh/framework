package scw.core.instance;

import java.util.LinkedHashMap;

import scw.core.instance.definition.InstanceDefinition;

public abstract class AbstractInstanceFactory<D extends InstanceDefinition>
		implements InstanceFactory {
	protected volatile LinkedHashMap<String, Object> singletonMap = new LinkedHashMap<String, Object>();

	public AbstractInstanceFactory() {
		singletonMap.put(InstanceFactory.class.getName(), this);
	}

	public D getDefinition(Class<?> clazz) {
		return getDefinition(clazz.getName());
	}

	public abstract D getDefinition(String name);

	public boolean isSingleton(String name) {
		if (singletonMap.containsKey(name)) {
			return true;
		}

		D definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return definition.isSingleton();
	}

	public boolean isSingleton(Class<?> clazz) {
		return isSingleton(clazz.getName());
	}

	public <T> T getInstance(Class<? extends T> clazz) {
		return getInstance(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		Object object = singletonMap.get(name);
		if (object != null) {
			return (T) object;
		}

		D definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		if (definition.isSingleton()) {
			object = singletonMap.get(definition.getId());
			if (object == null) {
				synchronized (singletonMap) {
					object = singletonMap.get(definition.getId());
					if (object == null) {
						object = createInternal(definition);
					}
				}
			}
		} else {
			object = createInternal(definition);
		}
		return (T) object;
	}

	protected abstract void afterCreation(long createTime, D definition,
			Object object) throws Exception;

	private Object createInternal(D definition) {
		long t = System.currentTimeMillis();
		Object obj;
		try {
			obj = definition.create();
			if (definition.isSingleton()) {
				singletonMap.put(definition.getId(), obj);
			}
			afterCreation(t, definition, obj);
		} catch (Exception e) {
			throw new InstanceException(definition.getId(), e);
		}
		return obj;
	}

	public boolean isInstance(String name) {
		if (singletonMap.containsKey(name)) {
			return true;
		}

		D definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return singletonMap.containsKey(definition.getId())
				|| definition.isInstance();
	}

	public boolean isInstance(Class<?> clazz) {
		return isInstance(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Object... params) {
		Object obj = singletonMap.get(name);
		if (obj != null) {
			return (T) obj;
		}

		D definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		if (definition.isSingleton()) {
			obj = singletonMap.get(definition.getId());
			if (obj == null) {
				synchronized (singletonMap) {
					obj = singletonMap.get(definition.getId());
					if (obj == null) {
						obj = createInternal(definition, params);
					}
				}
			}
		} else {
			obj = createInternal(definition, params);
		}
		return (T) obj;
	}

	private Object createInternal(D definition, Object... params) {
		long t = System.currentTimeMillis();
		Object obj;
		try {
			obj = definition.create(params);
			if (definition.isInstance()) {
				singletonMap.put(definition.getId(), obj);
			}
			afterCreation(t, definition, obj);
		} catch (Exception e) {
			throw new InstanceException(definition.getId(), e);
		}
		return obj;
	}

	public <T> T getInstance(Class<? extends T> type, Object... params) {
		return getInstance(type.getName(), params);
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Class<?>[] parameterTypes,
			Object... params) {
		Object obj = singletonMap.get(name);
		if (obj != null) {
			return (T) obj;
		}

		D definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		if (definition.isSingleton()) {
			obj = singletonMap.get(definition.getId());
			if (obj == null) {
				synchronized (singletonMap) {
					obj = singletonMap.get(definition.getId());
					if (obj == null) {
						obj = createInternal(definition, parameterTypes, params);
					}
				}
			}
		} else {
			obj = createInternal(definition, parameterTypes, params);
		}
		return (T) obj;
	}

	private Object createInternal(D definition, Class<?>[] parameterTypes,
			Object... params) {
		long t = System.currentTimeMillis();
		Object obj;
		try {
			obj = definition.create(parameterTypes, params);
			if (definition.isInstance()) {
				singletonMap.put(definition.getId(), obj);
			}
			afterCreation(t, definition, obj);
		} catch (Exception e) {
			throw new InstanceException(definition.getId(), e);
		}
		return obj;
	}

	public <T> T getInstance(Class<? extends T> type,
			Class<?>[] parameterTypes, Object... params) {
		return getInstance(type.getName(), parameterTypes, params);
	}
}
