package io.basc.framework.data.cas;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.data.kv.KeyValueOperations;
import io.basc.framework.util.CollectionUtils;

/**
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */

public interface CasKeyValueOperations<K, V>
		extends KeyValueOperations<K, V>, CasKeyOperations<K>, CasValueOperations<K, V> {
	CAS<V> gets(K key);

	default Map<K, CAS<V>> gets(Collection<K> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return Collections.emptyMap();
		}

		Map<K, CAS<V>> map = new LinkedHashMap<K, CAS<V>>(keys.size());
		for (K key : keys) {
			CAS<V> value = gets(key);
			if (value == null) {
				continue;
			}
			map.put(key, value);
		}
		return map;
	}
}
