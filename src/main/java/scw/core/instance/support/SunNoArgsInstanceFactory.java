package scw.core.instance.support;

import java.lang.reflect.Constructor;
import java.util.IdentityHashMap;

import scw.core.instance.NoArgsInstanceFactory;
import sun.reflect.ReflectionFactory;

@SuppressWarnings("restriction")
public class SunNoArgsInstanceFactory implements NoArgsInstanceFactory {
	private static final Constructor<?> CONSTRUCTOR;
	private static final ReflectionFactory REFLECTION_FACTORY = ReflectionFactory.getReflectionFactory();
	private volatile IdentityHashMap<Class<?>, Constructor<?>> constructorMap = new IdentityHashMap<Class<?>, Constructor<?>>();

	static {
		try {
			CONSTRUCTOR = Object.class.getConstructor();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Constructor<?> getConstructor(Class<?> type) {
		Constructor<?> constructor = constructorMap.get(type);
		if (constructor == null) {
			synchronized (constructorMap) {
				constructor = constructorMap.get(type);
				if (constructor == null) {
					constructor = REFLECTION_FACTORY.newConstructorForSerialization(type, CONSTRUCTOR);
					constructor.setAccessible(true);
					constructorMap.put(type, constructor);
				}
			}
		}
		return constructor;
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<T> type) {
		if (type == null) {
			return null;
		}

		try {
			return (T) getConstructor(type).newInstance();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		return (T) getInstance(ReflectionInstanceFactory.forName(name));
	}
}
