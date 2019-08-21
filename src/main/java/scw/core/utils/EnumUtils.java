package scw.core.utils;

import java.lang.reflect.Field;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class EnumUtils {
	private EnumUtils() {
	}

	public static Enum valueOf(Class<?> enumClass, String value) {
		return Enum.valueOf((Class<? extends Enum>) enumClass, value);
	}

	public static Enum valueOf(Class<? extends Enum> enumClass, Field field, Object value)
			throws IllegalArgumentException, IllegalAccessException {
		for (Enum e : ((Class<? extends Enum>) enumClass).getEnumConstants()) {
			Object v = field.get(e);
			if (ObjectUtils.equals(v, value)) {
				return e;
			}
		}
		return null;
	}
}
