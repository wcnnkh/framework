package scw.value;

import java.lang.reflect.Type;

import scw.core.utils.ClassUtils;
import scw.core.utils.TypeUtils;

public final class ValueUtils {
	private ValueUtils() {
	};

	public static <K> Object getValue(ValueFactory<K> valueFactory, K key, Type type, Object defaultValue) {
		Object v;
		if (TypeUtils.isPrimitive(type)) {
			v = valueFactory.getObject(key, ClassUtils.resolvePrimitiveIfNecessary((Class<?>) type));
		} else {
			v = valueFactory.getObject(key, type);
		}

		return v == null ? defaultValue : v;
	}

	public static <K, T> T getValue(ValueFactory<K> valueFactory, K key, Class<? extends T> type, T defaultValue) {
		@SuppressWarnings("unchecked")
		T v = (T) valueFactory.getObject(key, ClassUtils.resolvePrimitiveIfNecessary(type));
		return v == null ? defaultValue : v;
	}

	public static Object parse(String text, Class<?> type) {
		return new StringValue(text).getAsObject(type);
	}

	public static Object parse(String text, Type type) {
		return new StringValue(text).getAsObject(type);
	}

	/**
	 * 这并不是基本数据类型，这是指Value可以直接转换的类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBaseType(Type type) {
		if (TypeUtils.isClass(type)) {
			return isBaseType((Class<?>) type);
		}

		try {
			return isBaseType(ClassUtils.forName(type.toString(), ClassUtils.getDefaultClassLoader()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 这并不是基本数据类型，这是指Value可以直接转换的类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBaseType(Class<?> type) {
		return ClassUtils.isPrimitiveOrWrapper(type) || type == String.class || Number.class.isAssignableFrom(type)
				|| type.isEnum() || type == Class.class;
	}
}
