package scw.core.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.Verification;
import scw.core.instance.InstanceException;
import scw.core.instance.InstanceUtils;
import scw.core.instance.NoArgsInstanceFactory;
import scw.lang.UnsupportedException;

public final class CloneUtils {
	private CloneUtils() {
	};

	private static void setter(boolean invokeSetter, Class<?> clazz, Field field, Object target, Object value)
			throws Exception {
		if (invokeSetter) {
			ReflectionUtils.setFieldValue(clazz, field, Modifier.isStatic(field.getModifiers()) ? null : target, value);
		} else {
			ReflectionUtils.setAccessibleField(field);
			field.set(Modifier.isStatic(field.getModifiers()) ? null : target, value);
		}
	}

	public static <T> T clone(T source) {
		return clone(source, false, true);
	}

	public static <T> T clone(T source, boolean ignoreTransient) {
		return clone(source, ignoreTransient, true);
	}

	public static <T> T clone(T source, boolean ignoreTransient, boolean invokeSetter) {
		return clone(source, new IgnoreStaticFieldVerification(ignoreTransient), invokeSetter);
	}

	public static <T> T clone(T source, Verification<Field> ignoreVerification, boolean invokeSetter) {
		return clone(source, ignoreVerification, InstanceUtils.INSTANCE_FACTORY, invokeSetter);
	}

	/**
	 * 必须要存在默认的构造方法
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> T clone(T source, Verification<Field> ignoreVerification, NoArgsInstanceFactory instanceFactory,
			boolean invokeSetter) {
		try {
			return clone(source, ignoreVerification, true, instanceFactory, invokeSetter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Object cloneArray(Class<?> type, Object array, Verification<Field> ignoreVerification,
			boolean invokeCloneableMethod, NoArgsInstanceFactory instanceFactory, boolean invokeSetter) throws Exception {
		int size = Array.getLength(array);
		Object newArr = Array.newInstance(type.getComponentType(), size);
		for (int i = 0; i < size; i++) {
			Array.set(newArr, i, autoClone(Array.get(array, i), ignoreVerification, invokeCloneableMethod,
					instanceFactory, invokeSetter));
		}
		return newArr;
	}

	private static void cloneObject(Object obj, Object source, Verification<Field> ignoreVerification,
			boolean invokeCloneableMethod, NoArgsInstanceFactory instanceFactory, boolean invokeSetter) throws Exception {
		Class<?> clazz = source.getClass();
		while (clazz != null && clazz != Object.class) {
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
						v = cloneArray(field.getType(), v, ignoreVerification, invokeCloneableMethod, instanceFactory,
								invokeSetter);
					} else {
						v = autoClone(v, ignoreVerification, invokeCloneableMethod, instanceFactory, invokeSetter);
					}
				}

				setter(invokeSetter, clazz, field, source, v);
			}
			clazz = clazz.getSuperclass();
		}
	}

	private static Object cloneObject(Class<?> type, Object obj, Verification<Field> ignoreVerification,
			boolean invokeCloneableMethod, NoArgsInstanceFactory instanceFactory, boolean invokeSetter) throws Exception {
		if (!instanceFactory.isInstance(type)) {
			return obj;
		}

		Object t = instanceFactory.getInstance(type);
		if (t == null) {
			return obj;
		}

		cloneObject(obj, t, ignoreVerification, invokeCloneableMethod, instanceFactory, invokeSetter);
		return t;
	}

	@SuppressWarnings("unchecked")
	private static <T> T autoClone(T obj, Verification<Field> ignoreVerification, boolean invokeCloneableMethod,
			NoArgsInstanceFactory instanceFactory, boolean invokeSetter) throws Exception {
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
			return (T) cloneArray(type, obj, ignoreVerification, invokeCloneableMethod, instanceFactory, invokeSetter);
		} else if (invokeCloneableMethod && obj instanceof Cloneable) {
			Method method = ReflectionUtils.getMethod(type, "clone");
			if (method != null) {
				return (T) method.invoke(obj);
			}
		}
		return (T) cloneObject(type, obj, ignoreVerification, invokeCloneableMethod, instanceFactory, invokeSetter);
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
			NoArgsInstanceFactory instanceFactory, boolean invokeSetter) throws Exception {
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
			return (T) cloneArray(type, source, ignoreVerification, invokeCloneableMethod, instanceFactory,
					invokeSetter);
		} else if (invokeCloneableMethod && source instanceof Cloneable) {
			Method method = ReflectionUtils.getMethod(type, "clone");
			if (method != null) {
				return (T) method.invoke(source);
			}
		}

		if (!instanceFactory.isInstance(type)) {
			throw new InstanceException("无法进行实例化");
		}

		T t = (T) instanceFactory.getInstance(type);
		cloneObject(source, t, ignoreVerification, invokeCloneableMethod, instanceFactory, invokeSetter);
		return t;
	}

	public static <T> T copy(Object source, T target, Verification<Field> ignoreVerification, boolean clone,
			NoArgsInstanceFactory instanceFactory, boolean invokeSetter) throws Exception {
		Class<?> clz = target.getClass();
		while (clz != null && clz != Object.class) {
			for (Field field : clz.getDeclaredFields()) {
				if (ignoreVerification != null && ignoreVerification.verification(field)) {
					continue;
				}

				Field sourceField = ReflectionUtils.getField(source.getClass(), field.getName(), true);
				if (sourceField == null) {
					continue;
				}

				ReflectionUtils.setAccessibleField(sourceField);
				Object value = sourceField.get(source);
				if (value == null) {
					continue;
				}
				
				if (clone) {
					value = clone(value, ignoreVerification, instanceFactory, invokeSetter);
				}

				setter(invokeSetter, clz, sourceField, target, value);
			}
			clz = clz.getSuperclass();
		}
		return target;
	}

	public static <T> T copy(Object source, Class<T> clazz, Verification<Field> ignoreVerification,
			NoArgsInstanceFactory instanceFactory, boolean invokeSetter) {
		if (!instanceFactory.isInstance(clazz)) {
			throw new UnsupportedException("无法实例化:" + clazz);
		}

		T target = instanceFactory.getInstance(clazz);
		try {
			copy(source, target, ignoreVerification, false, instanceFactory, invokeSetter);
		} catch (Exception e) {
			throw new RuntimeException("复制时发生异常", e);
		}
		return target;
	}

	public static <T> T copy(Object source, T target, boolean invokeSetter) {
		try {
			return copy(source, target, new IgnoreStaticFieldVerification(false), false,
					InstanceUtils.INSTANCE_FACTORY, invokeSetter);
		} catch (Exception e) {
			throw new RuntimeException("复制时发生异常", e);
		}
	}

	public static <T> T copy(Object source, Class<T> clazz, boolean invokeSetter) {
		return copy(source, clazz, new IgnoreStaticFieldVerification(false), InstanceUtils.INSTANCE_FACTORY,
				invokeSetter);
	}

	public static <T> T copy(Object source, T target) {
		return copy(source, target, true);
	}

	public static <T> T copy(Object source, Class<T> clazz) {
		return copy(source, clazz, true);
	}
}
