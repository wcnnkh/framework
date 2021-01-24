package scw.core.utils;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.sql.Date;

public abstract class TypeUtils {
	public static boolean isString(Type type) {
		return type == String.class;
	}

	public static boolean isBoolean(Type type) {
		return type == boolean.class || type == Boolean.class;
	}

	public static boolean isShort(Type type) {
		return type == short.class || type == Short.class;
	}

	public static boolean isInt(Type type) {
		return type == int.class || type == Integer.class;
	}

	public static boolean isLong(Type type) {
		return type == long.class || type == Long.class;
	}

	public static boolean isChar(Type type) {
		return type == char.class || type == Character.class;
	}

	public static boolean isFloat(Type type) {
		return type == float.class || type == Float.class;
	}

	public static boolean isDouble(Type type) {
		return type == double.class || type == Double.class;
	}

	public static boolean isByte(Type type) {
		return type == byte.class || type == Byte.class;
	}

	public static boolean isPrimitive(Type type) {
		return type == int.class || type == long.class || type == boolean.class || type == float.class
				|| type == double.class || type == short.class || type == byte.class;
	}

	public static boolean isPrimitiveWrapper(Type type) {
		return type == Integer.class || type == Long.class || type == Boolean.class || type == Float.class
				|| type == Double.class || type == Short.class || type == Byte.class;
	}

	public static boolean isPrimitiveOrWrapper(Type type) {
		return isPrimitive(type) || isPrimitiveWrapper(type);
	}

	public static boolean isDateType(Type type) {
		return type == java.util.Date.class || type == Date.class;
	}

	public static boolean isClass(Type type) {
		return type instanceof Class;
	}
	
	/**
	 * 是否是数字类型，不包含char,boolean
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isNumberType(Type type) {
		if (isInt(type) || isLong(type) || isShort(type) || isFloat(type) || isDouble(type) || isByte(type)) {
			return true;
		}

		return isAssignableFrom(type, Number.class);
	}

	@SuppressWarnings("rawtypes")
	public static boolean isArray(Type type) {
		if (isClass(type)) {
			return ((Class) type).isArray();
		}

		return false;
	}

	public static boolean isAssignableFrom(Type type, Class<?> clazz) {
		if (type == clazz) {
			return true;
		}

		if (type == null || clazz == null) {
			return type == clazz;
		}

		try {
			ClassUtils.isAssignable(clazz, ClassUtils.forName(type.toString(), ClassUtils.getDefaultClassLoader()));
		} catch (ClassNotFoundException e) {
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean isInterface(Type type) {
		if (isClass(type)) {
			return ((Class) type).isInterface();
		} else {
			try {
				return ClassUtils.forName(type.toString(), ClassUtils.getDefaultClassLoader()).isInterface();
			} catch (ClassNotFoundException e) {
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean isAbstract(Type type) {
		if (isClass(type)) {
			return Modifier.isAbstract(((Class) type).getModifiers());
		} else {
			try {
				return Modifier.isAbstract(ClassUtils.forName(type.toString(), ClassUtils.getDefaultClassLoader()).getModifiers());
			} catch (ClassNotFoundException e) {
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static String getClassName(Type type) {
		if (isClass(type)) {
			return ((Class) type).getName();
		} else {
			try {
				return ClassUtils.forName(type.toString(), ClassUtils.getDefaultClassLoader()).getName();
			} catch (ClassNotFoundException e) {
			}
		}

		return type.toString();
	}

	/**
	 * 是否是数字类型，不包含char,boolean
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isNumber(Type type) {
		return isAssignableFrom(type, Number.class);
	}
}
