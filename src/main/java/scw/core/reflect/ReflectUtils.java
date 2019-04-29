package scw.core.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;

public final class ReflectUtils {
	private static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

	private ReflectUtils() {
	};

	public static <T, V> void setProperties(Class<T> type, T bean, Map<String, V> properties, boolean isPublicMethod,
			SetterMapper<V> mapper) {
		if (properties == null || properties.isEmpty()) {
			return;
		}

		for (Entry<String, V> entry : properties.entrySet()) {
			String methodName = "set" + StringUtils.toUpperCase(entry.getKey(), 0, 1);
			for (java.lang.reflect.Method method : isPublicMethod ? type.getMethods() : type.getDeclaredMethods()) {
				if (method.getParameterTypes().length != 1) {
					continue;
				}

				if (!method.getName().equals(methodName)) {
					continue;
				}

				Object v;
				try {
					v = mapper.mapper(bean, method, entry.getKey(), entry.getValue(), method.getParameterTypes()[0]);
					method.setAccessible(true);
					method.invoke(bean, v);
				} catch (Throwable e) {
					logger.error(
							"向对象" + type.getName() + "，插入name=" + entry.getKey() + ",value=" + entry.getValue() + "时异常",
							e);
				}
			}
		}
	}

	public static <T> Constructor<T> getConstructor(Class<T> type) throws NoSuchMethodException, SecurityException {
		Constructor<T> constructor = type.getConstructor();
		if (!Modifier.isPublic(constructor.getModifiers())) {
			constructor.setAccessible(true);
		}
		return constructor;
	}

	public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		Constructor<T> constructor = type.getConstructor(parameterTypes);
		if (!Modifier.isPublic(constructor.getModifiers())) {
			constructor.setAccessible(true);
		}
		return constructor;
	}

	public static Constructor<?> getConstructor(String className, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		return getConstructor(Class.forName(className), parameterTypes);
	}

	public static <T> Constructor<T> getConstructor(Class<T> type, String... parameterTypeNames)
			throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		return getConstructor(type, ClassUtils.forName(parameterTypeNames));
	}

	public static Constructor<?> getConstructor(String className, String... parameterTypes)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		return getConstructor(Class.forName(className), ClassUtils.forName(className));
	}

	/**
	 * 此方法可能返回空
	 * 
	 * @param type
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> findConstructorByParameters(Class<T> type, Object... params) {
		for (Constructor<?> constructor : type.getDeclaredConstructors()) {
			Class<?>[] types = constructor.getParameterTypes();
			if (types.length == params.length) {
				boolean find = true;
				for (int i = 0; i < types.length; i++) {
					Object v = params[i];
					if (v == null) {
						continue;
					}

					if (!ClassUtils.isAssignableValue(types[i], v)) {
						find = false;
					}
				}

				if (find) {
					if (!Modifier.isPublic(constructor.getModifiers())) {
						constructor.setAccessible(true);
					}
					return (Constructor<T>) constructor;
				}
			}
		}
		return null;
	}

	public static java.lang.reflect.Method getMethod(Class<?> type, String name, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		java.lang.reflect.Method method = type.getDeclaredMethod(name, parameterTypes);
		if (!Modifier.isPublic(type.getModifiers()) || !Modifier.isPublic(method.getModifiers())) {
			method.setAccessible(true);
		}
		return method;
	}

	public static <T> T newInstance(Class<T> type) {
		try {
			return getConstructor(type).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 必须要存在默认的构造方法
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> T clone(T obj) {
		try {
			return clone(obj, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Object cloneArray(Class<?> type, Object array, boolean ignoreTransient) throws Exception {
		int size = Array.getLength(array);
		Object newArr = Array.newInstance(type.getComponentType(), size);
		for (int i = 0; i < size; i++) {
			Array.set(newArr, i, clone(Array.get(array, i), ignoreTransient));
		}
		return newArr;
	}

	private static Object cloneObject(Class<?> type, Object obj, boolean ignoreTransient) throws Exception {
		if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
			return obj;
		}

		Constructor<?> constructor = null;
		try {
			constructor = getConstructor(type);
		} catch (NoSuchMethodException e) {
		}

		if (constructor == null) {
			return obj;
		}

		Object t = constructor.newInstance();
		Class<?> clazz = type;
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				if (ignoreTransient && Modifier.isTransient(field.getModifiers())) {
					continue;
				}

				field.setAccessible(true);
				Object v = field.get(obj);
				if (v == null) {
					continue;
				}

				if (!field.getType().isPrimitive() && !field.getType().isEnum()) {
					if (field.getType().isArray()) {
						v = cloneArray(field.getType(), v, ignoreTransient);
					} else {
						v = clone(v, ignoreTransient);
					}
				}
				field.set(Modifier.isStatic(field.getModifiers()) ? null : t, v);
			}
			clazz = clazz.getSuperclass();
		}
		return t;
	}

	/**
	 * 必须要存在默认的构造方法
	 * 
	 * @param obj
	 * @param ignoreTransient
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(T obj, boolean ignoreTransient) throws Exception {
		if (obj == null) {
			return null;
		}

		Class<?> type = obj.getClass();
		if (type.isPrimitive() || type.isEnum()) {
			return obj;
		} else if (type.isArray()) {
			return (T) cloneArray(type, obj, ignoreTransient);
		} else if (obj instanceof Cloneable) {
			try {
				return (T) getMethod(type, "clone").invoke(obj);
			} catch (NoSuchMethodException e) {
			}
		}

		return (T) cloneObject(type, obj, ignoreTransient);
	}
}