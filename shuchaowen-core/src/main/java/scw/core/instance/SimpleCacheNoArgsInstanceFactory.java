package scw.core.instance;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class SimpleCacheNoArgsInstanceFactory extends SimpleNoArgsInstanceFactory {
	private volatile Map<Class<?>, Constructor<?>> constructorMap = new HashMap<Class<?>, Constructor<?>>();

	@SuppressWarnings("unchecked")
	protected <T> Constructor<? extends T> getConstructor(Class<? extends T> clazz) {
		if (constructorMap.containsKey(clazz)) {
			return (Constructor<T>) constructorMap.get(clazz);
		}

		synchronized (constructorMap) {
			if (constructorMap.containsKey(clazz)) {
				return (Constructor<T>) constructorMap.get(clazz);
			}

			Constructor<?> constructor = super.getConstructor(clazz);
			if (constructor != null) {
				constructorMap.put(clazz, constructor);
			}
			return (Constructor<T>) constructor;
		}
	}
}
