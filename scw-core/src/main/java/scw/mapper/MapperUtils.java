package scw.mapper;

import java.lang.reflect.Field;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.utils.StringUtils;
import scw.instance.support.DefaultServiceLoaderFactory;
import scw.lang.NotSupportedException;
import scw.value.Value;
import scw.value.support.SystemPropertyFactory;

public class MapperUtils {
	private static final FieldFactory FIELD_FACTORY = new DefaultServiceLoaderFactory(new SystemPropertyFactory())
			.getServiceLoader(FieldFactory.class).first(() -> {
				return new DefaultFieldFactory(
						new String[] { Getter.BOOLEAN_GETTER_METHOD_PREFIX, Getter.DEFAULT_GETTER_METHOD_PREFIX },
						new String[] { Setter.DEFAULT_SETTER_METHOD_PREFIX });
			});

	private MapperUtils() {
	};

	public static FieldFactory getFieldFactory() {
		return FIELD_FACTORY;
	}
	
	public static Fields getFields(Class<?> entityClass) {
		return FIELD_FACTORY.getFields(entityClass);
	}

	public static String getGetterMethodName(Field field) {
		String name = field.getName();
		if (field.getType() == boolean.class) {
			if (name.length() > 2 && name.startsWith(Getter.BOOLEAN_GETTER_METHOD_PREFIX)
					&& Character.isUpperCase(name.charAt(2))) {
				return name;
			}

			return Getter.BOOLEAN_GETTER_METHOD_PREFIX + StringUtils.toUpperCase(name, 0, 1);
		} else {
			return Getter.DEFAULT_GETTER_METHOD_PREFIX + StringUtils.toUpperCase(name, 0, 1);
		}
	}

	public static String getSetterMethodName(Field field) {
		String name = field.getName();
		if (field.getType() == boolean.class) {
			if (name.length() > 2 && name.startsWith(Getter.BOOLEAN_GETTER_METHOD_PREFIX)
					&& Character.isUpperCase(name.charAt(2))) {
				return name.substring(2);
			}
		}
		return Setter.DEFAULT_SETTER_METHOD_PREFIX + StringUtils.toUpperCase(name, 0, 1);
	}

	public static void setValue(ConversionService conversionService, Object instance, scw.mapper.Field field,
			Object value) {
		if (!field.isSupportSetter()) {
			throw new NotSupportedException(field.toString());
		}

		Object valueToUse;
		if (value != null && value instanceof Value) {
			valueToUse = ((Value) value).getAsObject(field.getSetter().getGenericType());
		} else {
			valueToUse = conversionService.convert(value, value == null ? null : TypeDescriptor.forObject(value),
					new TypeDescriptor(field.getSetter()));
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

	public static final <T> T mapping(FieldFactory fieldFactory, Class<T> entityClass, scw.mapper.Field parentField,
			Mapping mapping) {
		return mapping.mapping(entityClass,
				fieldFactory.getFields(entityClass, parentField).accept(FieldFeature.SUPPORT_SETTER).accept(mapping),
				fieldFactory);
	}

	public static final <T> T mapping(Class<T> entityClass, scw.mapper.Field parentField, Mapping mapping) {
		return mapping(FIELD_FACTORY, entityClass, parentField, mapping);
	}

	public static final <T> T mapping(Class<T> entityClass, Mapping mapping) {
		return mapping(entityClass, null, mapping);
	}
}
