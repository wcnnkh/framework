package io.basc.framework.mapper;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.support.DefaultServiceLoaderFactory;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.Value;
import io.basc.framework.value.support.SystemPropertyFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class MapperUtils {
	private static final FieldFactory FIELD_FACTORY = new DefaultServiceLoaderFactory(new SystemPropertyFactory())
			.getServiceLoader(FieldFactory.class).first(() -> new DefaultFieldFactory());

	private MapperUtils() {
	};

	public static FieldFactory getFieldFactory() {
		return FIELD_FACTORY;
	}

	/**
	 * @see FieldFactory#getFields(Class)
	 * @param entityClass
	 * @return
	 */
	public static Fields getFields(Class<?> entityClass) {
		return getFieldFactory().getFields(entityClass);
	}

	/**
	 * @see FieldFactory#getFields(Class, io.basc.framework.mapper.Field)
	 * @param entityClass
	 * @param parentField
	 * @return
	 */
	public static Fields getFields(Class<?> entityClass, io.basc.framework.mapper.Field parentField) {
		return getFieldFactory().getFields(entityClass, parentField);
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

	public static void setValue(ConversionService conversionService, Object instance, io.basc.framework.mapper.Field field,
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
	public static boolean isExistValue(io.basc.framework.mapper.Field field, Object instance) {
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

	public static final <T> T mapping(FieldFactory fieldFactory, Class<T> entityClass, io.basc.framework.mapper.Field parentField,
			Mapping mapping) {
		return mapping.mapping(entityClass, fieldFactory.getFields(entityClass, parentField).all()
				.accept(FieldFeature.SUPPORT_SETTER).accept(mapping), fieldFactory);
	}

	public static final <T> T mapping(Class<T> entityClass, io.basc.framework.mapper.Field parentField, Mapping mapping) {
		return mapping(FIELD_FACTORY, entityClass, parentField, mapping);
	}

	public static final <T> T mapping(Class<T> entityClass, Mapping mapping) {
		return mapping(entityClass, null, mapping);
	}

	/**
	 * 递归解析
	 * 
	 * @param instance
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map toMap(Object instance) {
		return toMap(instance, true);
	}

	/**
	 * 将对象转换为map
	 * 
	 * @see ValueUtils#isBaseType(Class) 此类型无法解析
	 * @param instance
	 * @param recursion 是否递归
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Map toMap(Object instance, boolean recursion) {
		if (instance == null) {
			return Collections.emptyMap();
		}

		if (instance instanceof ToMap) {
			ToMap toMap = (ToMap) instance;
			return recursion ? parseMap(toMap.toMap()) : toMap.toMap();
		} else if (instance instanceof Map) {
			return parseMap((Map) instance);
		}

		if (Value.isBaseType(instance.getClass())) {
			throw new NotSupportedException(instance.getClass().getName());
		}

		Map<String, Object> valueMap = getFields(instance.getClass()).all().accept(FieldFeature.IGNORE_STATIC)
				.getValueMap(instance);
		return recursion ? parseMap(valueMap) : valueMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map parseMap(Map map) {
		if (CollectionUtils.isEmpty(map)) {
			return Collections.EMPTY_MAP;
		}

		Set<Entry> entries = map.entrySet();
		Map valueMap = new LinkedHashMap();
		for (Entry entry : entries) {
			valueMap.put(entry.getKey(), parseValue(entry.getValue()));
		}
		return valueMap;
	}

	@SuppressWarnings({ "rawtypes" })
	private static Object parseValue(Object value) {
		if (value == null) {
			return value;
		}

		if (Value.isBaseType(value.getClass())) {
			return value;
		} else if (value instanceof ToMap) {
			return parseMap(((ToMap) value).toMap());
		} else if (value instanceof Collection) {
			Collection list = (Collection) value;
			if (CollectionUtils.isEmpty(list)) {
				return value;
			}

			List<Object> newList = new ArrayList<Object>(list.size());
			for (Object v : list) {
				newList.add(parseValue(v));
			}
			return newList;
		} else if (value instanceof Map) {
			return parseMap((Map) value);
		} else if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			if (len == 0) {
				return value;
			}

			Object[] array = new Object[len];
			for (int i = 0; i < len; i++) {
				array[i] = parseValue(Array.get(value, i));
			}
			return array;
		} else {
			Map<String, Object> valueMap = getFields(value.getClass()).all().accept(FieldFeature.IGNORE_STATIC)
					.getValueMap(value);
			return parseMap(valueMap);
		}
	}

	public static void appendToMap(Properties properties, Map<String, String> map) {
		if (properties == null || map == null) {
			return;
		}

		for (Entry<Object, Object> entry : properties.entrySet()) {
			map.put(entry.getKey() == null ? null : entry.getKey().toString(),
					entry.getValue() == null ? null : entry.getValue().toString());
		}
	}

	public static String toString(Object instance) {
		if (instance == null) {
			return null;
		}
		return MapperUtils.getFields(instance.getClass()).entity().all().getValueMap(instance, true).toString();
	}
}