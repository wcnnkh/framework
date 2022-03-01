package io.basc.framework.data.object;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.CAS;
import io.basc.framework.data.kv.KeyCasOperations;
import io.basc.framework.util.CollectionUtils;

public interface ObjectCasOperations<K> extends ObjectOperations<K>, KeyCasOperations<K>, ObjectValueCasOperations<K> {

	default <T> CAS<T> gets(Class<T> type, K key) {
		return gets(TypeDescriptor.valueOf(type), key);
	}

	<T> CAS<T> gets(TypeDescriptor type, K key);

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
}
