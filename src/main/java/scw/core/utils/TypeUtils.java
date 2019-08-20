package scw.core.utils;

import java.lang.reflect.Type;

public final class TypeUtils {
	private TypeUtils() {
	}

	public static boolean isBooleanType(Type type) {
		return type == boolean.class || type == Boolean.class;
	}

	public static boolean isShortType(Type type) {
		return type == short.class || type == Short.class;
	}

	public static boolean isIntegerType(Type type) {
		return type == int.class || type == Integer.class;
	}

	public static boolean isLongType(Type type) {
		return type == long.class || type == Long.class;
	}

	public static boolean isCharType(Type type) {
		return type == char.class || type == Character.class;
	}

	public static boolean isFloatType(Type type) {
		return type == float.class || type == Float.class;
	}

	public static boolean isDoubleType(Type type) {
		return type == double.class || type == Double.class;
	}

	public static boolean isByteType(Type type) {
		return type == byte.class || type == Byte.class;
	}

	public static boolean isPrimitiveType(Type type) {
		return type == int.class || type == long.class || type == boolean.class || type == float.class
				|| type == double.class || type == short.class || type == byte.class;
	}

	public static boolean isPrimitiveWrapperType(Type type) {
		return type == Integer.class || type == Long.class || type == Boolean.class || type == Float.class
				|| type == Double.class || type == Short.class || type == Byte.class;
	}

	public static boolean isPrimitiveOrWrapperType(Type type) {
		return isPrimitiveType(type) || isPrimitiveOrWrapperType(type);
	}
}
