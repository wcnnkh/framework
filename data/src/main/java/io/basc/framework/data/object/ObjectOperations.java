package io.basc.framework.data.object;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.KeyOperations;
import io.basc.framework.util.CollectionUtils;

/**
 * 存储管理
 * 
 * @author wcnnkh
 *
 */

public interface ObjectOperations<K> extends KeyOperations<K>, ObjectValueOperations<K> {
	default <T> T get(Class<T> type, K key) {
		return get(TypeDescriptor.valueOf(type), key);
	}

	<T> T get(TypeDescriptor type, K key);

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
}
