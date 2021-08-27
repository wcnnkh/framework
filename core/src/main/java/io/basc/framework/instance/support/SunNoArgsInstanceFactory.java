package io.basc.framework.instance.support;

import io.basc.framework.core.utils.ClassUtils;
import io.basc.framework.instance.InstanceException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

public class SunNoArgsInstanceFactory extends AbstractNoArgsInstanceFactory {
	private static final Constructor<?> CONSTRUCTOR;
	private volatile IdentityHashMap<Class<?>, Constructor<?>> constructorMap = new IdentityHashMap<Class<?>, Constructor<?>>();
	private static final Object INVOKE_INSTANCE;
	private static final Method METHOD;

	static {
		try {
			CONSTRUCTOR = Object.class.getConstructor();
			Class<?> clz = ClassUtils.forName("sun.reflect.ReflectionFactory", null);
			Method method = clz.getMethod("getReflectionFactory");
			INVOKE_INSTANCE = method.invoke(null);
			METHOD = clz.getMethod("newConstructorForSerialization",
					Class.class, Constructor.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Constructor<?> getConstructor(Class<?> type) throws Exception {
		Constructor<?> constructor = constructorMap.get(type);
		if (constructor == null) {
			synchronized (constructorMap) {
				constructor = constructorMap.get(type);
				if (constructor == null) {
					constructor = (Constructor<?>) METHOD.invoke(
							INVOKE_INSTANCE, type, CONSTRUCTOR);
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
		
		if(!isInstance(type)){
			return null;
		}

		Constructor<?> constructor = null;
		try {
			constructor = getConstructor(type);
		} catch (Exception e) {
			throw new InstanceException(type.getName(), e);
		}

		if (constructor == null) {
			return null;
		}

		try {
			return (T) constructor.newInstance();
		} catch (Exception e) {
			throw new InstanceException(type.getName(), e);
		}
	}
	
	public boolean isInstance(Class<?> clazz) {
		return isInstance(clazz);
	}
}
