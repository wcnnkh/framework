package scw.core.utils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

import scw.util.value.StringValue;

public class StringParse {

	public static Object defaultParse(String text, Class<?> type) {
		return new StringValue(text).getAsObject(type);
	}

	public static Object defaultParse(String text, Type type) {
		return new StringValue(text).getAsObject(type);
	}

	public static boolean isCommonType(Type type) {
		if (TypeUtils.isPrimitiveOrWrapper(type)) {
			return true;
		}

		if (TypeUtils.isClass(type)) {
			return isCommonType((Class<?>) type);
		}

		try {
			return isCommonType(ClassUtils.forName(type.toString(),
					ClassUtils.getDefaultClassLoader()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean isCommonType(Class<?> type) {
		return type.isArray() || type.isEnum()
				|| Collection.class.isAssignableFrom(type)
				|| Map.class.isAssignableFrom(type)
				|| java.util.Date.class.isAssignableFrom(type)
				|| BigInteger.class.isAssignableFrom(type)
				|| BigDecimal.class.isAssignableFrom(type);
	}
}
