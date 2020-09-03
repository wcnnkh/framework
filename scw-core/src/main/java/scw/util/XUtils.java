package scw.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import scw.core.Converter;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;

public final class XUtils {
	private XUtils() {
	};

	public static String getUUID() {
		return StringUtils.removeChar(UUID.randomUUID().toString(), '-');
	}

	public static <T, R> T useResource(ResourceFactory<R> resourceFactory, Converter<R, T> converter) {
		R resource = null;
		try {
			resource = resourceFactory.getResource();
			return converter.convert(resource);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			resourceFactory.release(resource);
		}
	}

	public static <T, R> T execute(ResourceFactory<R> resourceFactory, Converter<R, T> resourceConverter) {
		R r = null;
		try {
			r = resourceFactory.getResource();
			return resourceConverter.convert(r);
		} finally {
			resourceFactory.release(r);
		}
	}

	@SuppressWarnings("unchecked")
	public static <K> Map<K, Object> toMap(ToMap<? extends K, ?> toMap) {
		if (toMap == null) {
			return null;
		}

		Map<? extends K, ?> map = toMap.toMap();
		if (map == null) {
			return null;
		}

		if (map.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		Map<K, Object> valueMap = new LinkedHashMap<K, Object>();
		for (Entry<? extends K, ?> entry : map.entrySet()) {
			valueMap.put(entry.getKey(), toParameterMapTransformation(entry.getValue()));
		}
		return valueMap;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object toParameterMapTransformation(Object value) {
		if (value == null) {
			return value;
		}

		if (value instanceof ToMap) {
			return toMap((ToMap) value);
		} else if (value instanceof Collection) {
			Collection list = (Collection) value;
			if (CollectionUtils.isEmpty(list)) {
				return value;
			}

			List<Object> newList = new ArrayList<Object>(list.size());
			for (Object v : list) {
				Object tmp = toParameterMapTransformation(v);
				if (tmp == null) {
					continue;
				}
				newList.add(tmp);
			}
			return newList;
		} else if (value instanceof Map) {
			Map map = (Map) value;
			if (CollectionUtils.isEmpty(map)) {
				return value;
			}

			Set<Map.Entry> set = map.entrySet();
			for (Map.Entry entry : set) {
				entry.setValue(toParameterMapTransformation(entry.getValue()));
			}
		} else if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			if (len == 0) {
				return value;
			}

			for (int i = 0; i < len; i++) {
				Object v = Array.get(value, i);
				Array.set(value, i, toParameterMapTransformation(v));
			}
		}
		return value;
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

	public static <T> T use(T... objs) {
		for (T obj : objs) {
			if (obj != null) {
				return obj;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getTarget(Object wrapper, Class<T> targetType) {
		if (targetType.isInstance(wrapper)) {
			return (T) wrapper;
		}

		if (wrapper instanceof Target) {
			return ((Target) wrapper).getTarget(targetType);
		}
		return null;
	}
}