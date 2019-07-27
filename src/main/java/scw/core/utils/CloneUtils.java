package scw.core.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectUtils;

public final class CloneUtils {
	private CloneUtils() {
	};

	/**
	 * 必须要存在默认的构造方法
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> T clone(T source, boolean ignoreTransient) {
		try {
			return clone(source, true, ignoreTransient, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Object cloneArray(Class<?> type, Object array,
			boolean ignoreStatic, boolean ignoreTransient,
			boolean invokeCloneableMethod) throws Exception {
		int size = Array.getLength(array);
		Object newArr = Array.newInstance(type.getComponentType(), size);
		for (int i = 0; i < size; i++) {
			Array.set(
					newArr,
					i,
					autoClone(Array.get(array, i), ignoreStatic,
							ignoreTransient, invokeCloneableMethod));
		}
		return newArr;
	}

	private static void cloneObject(Object obj, Object source,
			boolean ignoreStatic, boolean ignoreTransient,
			boolean invokeCloneableMethod) throws Exception {
		Class<?> clazz = source.getClass();
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				if (ignoreStatic && Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				if (ignoreTransient
						&& Modifier.isTransient(field.getModifiers())) {
					continue;
				}

				field.setAccessible(true);
				Object v = field.get(obj);
				if (v == null) {
					continue;
				}

				if (!field.getType().isPrimitive() && !field.getType().isEnum()) {
					if (field.getType().isArray()) {
						v = cloneArray(field.getType(), v, ignoreStatic,
								ignoreTransient, invokeCloneableMethod);
					} else {
						v = autoClone(v, ignoreStatic, ignoreTransient,
								invokeCloneableMethod);
					}
				}
				field.set(Modifier.isStatic(field.getModifiers()) ? null
						: source, v);
			}
			clazz = clazz.getSuperclass();
		}
	}

	private static Object cloneObject(Class<?> type, Object obj,
			boolean ignoreStatic, boolean ignoreTransient,
			boolean invokeCloneableMethod) throws Exception {
		if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
			return obj;
		}

		Constructor<?> constructor = ReflectUtils.getConstructor(type, false);
		if (constructor == null) {
			return obj;
		}

		Object t = constructor.newInstance();
		cloneObject(obj, t, ignoreStatic, ignoreTransient,
				invokeCloneableMethod);
		return t;
	}

	@SuppressWarnings("unchecked")
	private static <T> T autoClone(T obj, boolean ignoreStatic,
			boolean ignoreTransient, boolean invokeCloneableMethod)
			throws Exception {
		if (obj == null) {
			return null;
		}

		if (obj instanceof scw.core.Cloneable) {
			return (T) ((scw.core.Cloneable) obj).clone();
		}

		Class<?> type = obj.getClass();
		if (type.isPrimitive() || type.isEnum()) {
			return obj;
		} else if (type.isArray()) {
			return (T) cloneArray(type, obj, ignoreStatic, ignoreTransient,
					invokeCloneableMethod);
		} else if (invokeCloneableMethod && obj instanceof Cloneable) {
			try {
				return (T) ReflectUtils.getMethod(type, false, "clone").invoke(
						obj);
			} catch (NoSuchMethodException e) {
			}
		}

		return (T) cloneObject(type, obj, ignoreStatic, ignoreTransient,
				invokeCloneableMethod);
	}

	/**
	 * 
	 * @param obj
	 * @param ignoreStatic
	 * @param ignoreTransient
	 * @param invokeCloneMethod
	 *            如果对象实现了java.lang.Cloneable接口，是否反射调用clone方法
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(T source, boolean ignoreStatic,
			boolean ignoreTransient, boolean invokeCloneableMethod)
			throws Exception {
		if (source == null) {
			return null;
		}

		if (source instanceof scw.core.Cloneable) {
			return (T) ((scw.core.Cloneable) source).clone();
		}

		Class<?> type = source.getClass();
		if (type.isPrimitive() || type.isEnum()) {
			return source;
		} else if (type.isArray()) {
			return (T) cloneArray(type, source, ignoreStatic, ignoreTransient,
					invokeCloneableMethod);
		} else if (invokeCloneableMethod && source instanceof Cloneable) {
			try {
				return (T) ReflectUtils.getMethod(type, false, "clone").invoke(
						source);
			} catch (NoSuchMethodException e) {
			}
		}

		T t = InstanceUtils.newInstance(type);
		cloneObject(source, t, ignoreStatic, ignoreTransient,
				invokeCloneableMethod);
		return t;
	}
}
