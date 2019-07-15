package scw.core.reflect.instance;

import java.lang.reflect.Constructor;
import java.util.IdentityHashMap;

import scw.core.InstanceFactory;
import sun.reflect.ReflectionFactory;

@SuppressWarnings("restriction")
public class SunInstanceFactory implements InstanceFactory {
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

	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<T> type) {
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

		try {
			return (T) constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
