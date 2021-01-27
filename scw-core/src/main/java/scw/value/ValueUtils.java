package scw.value;

import java.lang.reflect.Type;

import scw.core.utils.ClassUtils;
import scw.value.factory.ConvertibleValueFactory;

public final class ValueUtils {
	public static final Value[] EMPTY_ARRAY = new Value[0];
	
	private ValueUtils() {
	};

	public static <K> Object getValue(ConvertibleValueFactory<K> valueFactory, K key, Type type, Object defaultValue) {
		Object v;
		if (ClassUtils.isPrimitive(type)) {
			v = valueFactory.getObject(key, ClassUtils.resolvePrimitiveIfNecessary((Class<?>) type));
		} else {
			v = valueFactory.getObject(key, type);
		}

		return v == null ? defaultValue : v;
	}

	public static <K, T> T getValue(ConvertibleValueFactory<K> valueFactory, K key, Class<? extends T> type, T defaultValue) {
		@SuppressWarnings("unchecked")
		T v = (T) valueFactory.getObject(key, ClassUtils.resolvePrimitiveIfNecessary(type));
		return v == null ? defaultValue : v;
	}

	public static <T> T parse(String text, Class<T> type) {
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
	public static boolean isBaseType(Class<?> type) {
		return ClassUtils.isPrimitiveOrWrapper(type) || type == String.class || Number.class.isAssignableFrom(type)
				|| type.isEnum() || type == Class.class;
	}
}
