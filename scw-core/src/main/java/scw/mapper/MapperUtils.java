package scw.mapper;

import java.lang.reflect.Field;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.utils.StringUtils;
import scw.lang.NotSupportedException;
import scw.value.Value;

public class MapperUtils {
	private static final String BOOLEAN_GETTER_METHOD_PREFIX = "is";
	private static final String DEFAULT_GETTER_METHOD_PREFIX = "get";
	private static final String DEFAULT_SETTER_METHOD_PREFIX = "set";
	private static final Mapper MAPPER = new Mapper(new String[]{BOOLEAN_GETTER_METHOD_PREFIX, DEFAULT_GETTER_METHOD_PREFIX},
			new String[]{DEFAULT_SETTER_METHOD_PREFIX});

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
	
	public static void setValue(ConversionService conversionService, Object instance, scw.mapper.Field field, Object value){
		if(!field.isSupportSetter()){
			throw new NotSupportedException(field.toString());
		}
		
		Object valueToUse;
		if(value != null && value instanceof Value){
			valueToUse = ((Value)value).getAsObject(field.getSetter().getGenericType());
		}else{
			valueToUse = conversionService.convert(value, value == null? null:TypeDescriptor.forObject(value), new TypeDescriptor(field.getSetter()));
		}
		field.getSetter().set(instance, valueToUse);
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
}
