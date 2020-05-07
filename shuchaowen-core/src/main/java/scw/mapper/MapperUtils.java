package scw.mapper;

import java.lang.reflect.Field;
import java.util.Arrays;

import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.util.cache.LocalCacheType;
import scw.util.value.ValueUtils;

public class MapperUtils {
	private static final String BOOLEAN_GETTER_METHOD_PREFIX = "is";
	private static final String DEFAULT_GETTER_METHOD_PREFIX = "get";
	private static final String DEFAULT_SETTER_METHOD_PREFIX = "set";
	private static final Mapper MAPPER = new DefaultMapper(
			Arrays.asList(BOOLEAN_GETTER_METHOD_PREFIX,
					DEFAULT_GETTER_METHOD_PREFIX),
			Arrays.asList(DEFAULT_SETTER_METHOD_PREFIX),
			LocalCacheType.CONCURRENT_REFERENCE_HASH_MAP);
	
	private MapperUtils(){};

	public static Mapper getMapper() {
		return MAPPER;
	}

	public static String getGetterMethodName(Field field, String name){
		if (TypeUtils.isBoolean(field.getType())) {
			if(name.length() > 2 && name.startsWith(BOOLEAN_GETTER_METHOD_PREFIX) && Character.isUpperCase(name.charAt(2))){
				return name;
			}
			
			return BOOLEAN_GETTER_METHOD_PREFIX + StringUtils.toUpperCase(name, 0, 1);
		} else {
			return DEFAULT_GETTER_METHOD_PREFIX + StringUtils.toUpperCase(name, 0, 1);
		}
	}

	public static String getSetterMethodName(Field field, String name) {
		return DEFAULT_SETTER_METHOD_PREFIX + StringUtils.toUpperCase(name, 0, 1);
	}
	
	public static void setStringValue(FieldContext fieldContext, Object instance, String value) throws Exception{
		fieldContext.getField().getSetter().set(instance, ValueUtils.parse(value, fieldContext.getField().getSetter().getGenericType()));
	}
}
