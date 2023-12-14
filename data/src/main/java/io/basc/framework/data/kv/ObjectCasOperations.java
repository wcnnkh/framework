package io.basc.framework.data.kv;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.memory.CAS;
import io.basc.framework.util.CollectionUtils;

public interface ObjectCasOperations<K> extends ObjectOperations<K>, KeyValueCasOperations<K, Object> {

	@SuppressWarnings("unchecked")
	default <T> CAS<T> gets(TypeDescriptor type, K key) {
		CAS<Object> value = gets(key);
		if (value == null) {
			return null;
		}
		return new CAS<T>(value.getCas(),
				(T) getConversionService().convert(value.getValue(), TypeDescriptor.forObject(value.getValue()), type));
	}

	@Override
	default boolean cas(K key, Object value, long cas) {
		return cas(key, value, TypeDescriptor.forObject(value), cas);
	}

	default <T> CAS<T> gets(Class<T> type, K key) {
		return gets(TypeDescriptor.valueOf(type), key);
	}

	default <T> Map<K, CAS<T>> gets(TypeDescriptor type, Collection<K> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return Collections.emptyMap();
		}

		Map<K, CAS<T>> map = new LinkedHashMap<K, CAS<T>>(keys.size());
		for (K key : keys) {
			CAS<T> value = gets(type, key);
			if (value == null) {
				continue;
			}
			map.put(key, value);
		}
		return map;
	}

	default <T> boolean cas(K key, T value, Class<T> valueType, long cas) {
		return cas(key, value, TypeDescriptor.valueOf(valueType), cas);
	}

	boolean cas(K key, Object value, TypeDescriptor valueType, long cas);
}
