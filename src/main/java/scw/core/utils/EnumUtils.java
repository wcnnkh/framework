package scw.core.utils;

import java.lang.reflect.Field;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class EnumUtils {
	private EnumUtils() {
	}

	public static Enum valueOf(Class<? extends Enum> enumClass, String value) {
		return Enum.valueOf(enumClass, value.toString());
	}

	public static Enum valueOf(Class<? extends Enum> enumClass, Field field, Object value)
			throws IllegalArgumentException, IllegalAccessException {
		for (Enum e : enumClass.getEnumConstants()) {
			Object v = field.get(e);
			if (ObjectUtils.equals(v, value)) {
				return e;
			}
		}
		return null;
	}
}
