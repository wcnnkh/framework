package io.basc.framework.data.kv;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.support.GlobalConversionService;
import io.basc.framework.util.CollectionUtils;

public interface ObjectOperations<K> extends KeyValueOperations<K, Object> {

	default ConversionService getConversionService() {
		return GlobalConversionService.getInstance();
	}

	default <T> T get(Class<T> type, K key) {
		return get(TypeDescriptor.valueOf(type), key);
	}

	@SuppressWarnings("unchecked")
	default <T> T get(TypeDescriptor type, K key) {
		Object value = get(key);
		return (T) getConversionService().convert(value, TypeDescriptor.forObject(value), type);
	}

	@Override
	default void set(K key, Object value) {
		set(key, value, TypeDescriptor.forObject(value));
	}

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return setIfAbsent(key, value, TypeDescriptor.forObject(value));
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return setIfPresent(key, value, TypeDescriptor.forObject(value));
	}

	default <T> Map<K, T> get(Class<T> type, Collection<K> keys) {
		return get(TypeDescriptor.valueOf(type), keys);
	}

	default <T> Map<K, T> get(TypeDescriptor type, Collection<K> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return Collections.emptyMap();
		}

		Map<K, T> map = new LinkedHashMap<K, T>(keys.size());
		for (K key : keys) {
			T value = get(type, key);
			if (value == null) {
				continue;
			}

			map.put(key, value);
		}
		return map;
	}

	default <T> boolean setIfPresent(K key, T value, Class<T> valueType) {
		return setIfPresent(key, value, TypeDescriptor.valueOf(valueType));
	}

	boolean setIfPresent(K key, Object value, TypeDescriptor valueType);

	default <T> void set(K key, T value, Class<T> valueType) {
		set(key, value, TypeDescriptor.valueOf(valueType));
	}

	void set(K key, Object value, TypeDescriptor valueType);

	default <T> boolean setIfAbsent(K key, T value, Class<T> valueType) {
		return setIfAbsent(key, value, TypeDescriptor.valueOf(valueType));
	}

	boolean setIfAbsent(K key, Object value, TypeDescriptor valueType);
}
