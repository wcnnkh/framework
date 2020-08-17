package scw.mapper;

import java.lang.reflect.Field;
import java.util.Arrays;

import scw.core.utils.StringUtils;
import scw.lang.Description;
import scw.util.cache.LocalCacheType;
import scw.value.ValueUtils;

public class MapperUtils {
	private static final String BOOLEAN_GETTER_METHOD_PREFIX = "is";
	private static final String DEFAULT_GETTER_METHOD_PREFIX = "get";
	private static final String DEFAULT_SETTER_METHOD_PREFIX = "set";
	private static final Mapper MAPPER = new DefaultMapper(
			Arrays.asList(BOOLEAN_GETTER_METHOD_PREFIX, DEFAULT_GETTER_METHOD_PREFIX),
			Arrays.asList(DEFAULT_SETTER_METHOD_PREFIX), LocalCacheType.CONCURRENT_REFERENCE_HASH_MAP);

	private MapperUtils() {
	};

	public static Mapper getMapper() {
		return MAPPER;
	}

	public static String getGetterMethodName(Field field) {
		String name = field.getName();
		if (field.getType() == boolean.class) {
			if (name.length() > 2 && name.startsWith(BOOLEAN_GETTER_METHOD_PREFIX)
					&& Character.isUpperCase(name.charAt(2))) {
				return name;
			}

			return BOOLEAN_GETTER_METHOD_PREFIX + StringUtils.toUpperCase(name, 0, 1);
		} else {
			return DEFAULT_GETTER_METHOD_PREFIX + StringUtils.toUpperCase(name, 0, 1);
		}
	}

	public static String getSetterMethodName(Field field) {
		String name = field.getName();
		if (field.getType() == boolean.class) {
			if (name.length() > 2 && name.startsWith(BOOLEAN_GETTER_METHOD_PREFIX)
					&& Character.isUpperCase(name.charAt(2))) {
				return name.substring(2);
			}
		}
		return DEFAULT_SETTER_METHOD_PREFIX + StringUtils.toUpperCase(name, 0, 1);
	}

	public static void setStringValue(scw.mapper.Field field, Object instance, String value) {
		field.getSetter().set(instance, ValueUtils.parse(value, field.getSetter().getGenericType()));
	}

	/**
	 * 是否存在值
	 * 
	 * @param field
	 * @param instance
	 * @return
	 */
	public static boolean isExistValue(scw.mapper.Field field, Object instance) {
		if (!field.isSupportGetter()) {
			return false;
		}
		
		if (field.getGetter().getType().isPrimitive()) {// 如果是值类型，那么是不可能为空的
			Object value = field.getGetter().get(instance);
			if (value != null && value instanceof Number) {
				return ((Number) value).doubleValue() != 0;
			}
			return false;
		} else {
			return field.getGetter().get(instance) != null;
		}
	}

	public static boolean isDescription(FieldDescriptor descriptor) {
		return descriptor.getAnnotatedElement().getAnnotation(Description.class) != null;
	}
}
