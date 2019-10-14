package scw.core.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import scw.core.Verification;
import scw.core.exception.NotSupportException;
import scw.core.instance.InstanceException;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;

public final class CloneUtils {
	private CloneUtils() {
	};

	public static <T> T clone(T source, boolean ignoreTransient) {
		return clone(source, new IgnoreStaticFieldVerification(ignoreTransient));
	}

	public static <T> T clone(T source, Verification<Field> ignoreVerification) {
		return clone(source, ignoreVerification, InstanceUtils.REFLECTION_INSTANCE_FACTORY);
	}

	/**
	 * 必须要存在默认的构造方法
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> T clone(T source, Verification<Field> ignoreVerification, InstanceFactory instanceFactory) {
		try {
			return clone(source, ignoreVerification, true, instanceFactory);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Object cloneArray(Class<?> type, Object array, Verification<Field> ignoreVerification,
			boolean invokeCloneableMethod, InstanceFactory instanceFactory) throws Exception {
		int size = Array.getLength(array);
		Object newArr = Array.newInstance(type.getComponentType(), size);
		for (int i = 0; i < size; i++) {
			Array.set(newArr, i,
					autoClone(Array.get(array, i), ignoreVerification, invokeCloneableMethod, instanceFactory));
		}
		return newArr;
	}

	private static void cloneObject(Object obj, Object source, Verification<Field> ignoreVerification,
			boolean invokeCloneableMethod, InstanceFactory instanceFactory) throws Exception {
		Class<?> clazz = source.getClass();
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				if (ignoreVerification != null && ignoreVerification.verification(field)) {
					continue;
				}

				field.setAccessible(true);
				Object v = field.get(obj);
				if (v == null) {
					continue;
				}

				if (!field.getType().isPrimitive() && !field.getType().isEnum()) {
					if (field.getType().isArray()) {
						v = cloneArray(field.getType(), v, ignoreVerification, invokeCloneableMethod, instanceFactory);
					} else {
						v = autoClone(v, ignoreVerification, invokeCloneableMethod, instanceFactory);
					}
				}
				field.set(Modifier.isStatic(field.getModifiers()) ? null : source, v);
			}
			clazz = clazz.getSuperclass();
		}
	}

	private static Object cloneObject(Class<?> type, Object obj, Verification<Field> ignoreVerification,
			boolean invokeCloneableMethod, InstanceFactory instanceFactory) throws Exception {
		if (!instanceFactory.isInstance(type)) {
			return obj;
		}

		Object t = instanceFactory.getInstance(type);
		if (t == null) {
			return obj;
		}

		cloneObject(obj, t, ignoreVerification, invokeCloneableMethod, instanceFactory);
		return t;
	}

	@SuppressWarnings("unchecked")
	private static <T> T autoClone(T obj, Verification<Field> ignoreVerification, boolean invokeCloneableMethod,
			InstanceFactory instanceFactory) throws Exception {
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
			return (T) cloneArray(type, obj, ignoreVerification, invokeCloneableMethod, instanceFactory);
		} else if (invokeCloneableMethod && obj instanceof Cloneable) {
			try {
				return (T) ReflectUtils.getMethod(type, false, "clone").invoke(obj);
			} catch (NoSuchMethodException e) {
			}
		}

		return (T) cloneObject(type, obj, ignoreVerification, invokeCloneableMethod, instanceFactory);
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
	public static <T> T clone(T source, Verification<Field> ignoreVerification, boolean invokeCloneableMethod,
			InstanceFactory instanceFactory) throws Exception {
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
			return (T) cloneArray(type, source, ignoreVerification, invokeCloneableMethod, instanceFactory);
		} else if (invokeCloneableMethod && source instanceof Cloneable) {
			try {
				return (T) ReflectUtils.getMethod(type, false, "clone").invoke(source);
			} catch (NoSuchMethodException e) {
			}
		}

		if (!instanceFactory.isInstance(type)) {
			throw new InstanceException("无法进行实例化");
		}

		T t = (T) instanceFactory.getInstance(type);
		cloneObject(source, t, ignoreVerification, invokeCloneableMethod, instanceFactory);
		return t;
	}

	public static <T> T copy(Object source, T target, Verification<Field> ignoreVerification, boolean clone,
			InstanceFactory instanceFactory) throws IllegalArgumentException, IllegalAccessException {
		Class<?> clz = target.getClass();
		while (clz != null && clz != Object.class) {
			for (Field field : clz.getDeclaredFields()) {
				if (ignoreVerification != null && ignoreVerification.verification(field)) {
					continue;
				}

				Field sourceField = ReflectUtils.getField(source.getClass(), field.getName(), true);
				if (sourceField == null) {
					continue;
				}

				if (field.getGenericType() != sourceField.getGenericType()) {
					continue;
				}

				ReflectUtils.setAccessibleField(sourceField);
				Object value = sourceField.get(source);
				if (value == null) {
					continue;
				}

				ReflectUtils.setAccessibleField(field);
				field.set(target, clone ? clone(value, ignoreVerification, instanceFactory) : value);
			}
			clz = clz.getSuperclass();
		}
		return target;
	}

	public static <T> T copy(Object source, Class<T> clazz, Verification<Field> ignoreVerification,
			InstanceFactory instanceFactory) {
		if (!instanceFactory.isInstance(clazz)) {
			throw new NotSupportException("无法实例化:" + clazz);
		}

		T target = instanceFactory.getInstance(clazz);
		try {
			copy(source, target, ignoreVerification, false, instanceFactory);
		} catch (Exception e) {
			throw new RuntimeException("复制时发生异常", e);
		}
		return target;
	}

	public static <T> T copy(Object source, T target) {
		try {
			return copy(source, target, new IgnoreStaticFieldVerification(false), false,
					InstanceUtils.REFLECTION_INSTANCE_FACTORY);
		} catch (Exception e) {
			throw new RuntimeException("复制时发生异常", e);
		}
	}

	public static <T> T copy(Object source, Class<T> clazz) {
		return copy(source, clazz, new IgnoreStaticFieldVerification(false), InstanceUtils.REFLECTION_INSTANCE_FACTORY);
	}
}
