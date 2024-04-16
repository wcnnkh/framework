package io.basc.framework.data.kv;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.Encoder;
import io.basc.framework.data.domain.CAS;
import io.basc.framework.util.CollectionUtils;

public interface KeyValueCasOperationsWrapper<K, V>
		extends KeyValueCasOperations<K, V>, KeyValueOperationsWrapper<K, V>, KeyCasOperationsWrapper<K> {
	KeyValueCasOperations<K, V> getSourceOperations();

	@Override
	default CAS<V> gets(K key) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		CAS<V> value = getSourceOperations().gets(keyFomatter == null ? key : keyFomatter.encode(key));
		Codec<V, V> valueFomatter = getValueFomatter();
		return (value == null || valueFomatter == null) ? value : value.convert((e) -> valueFomatter.decode(e));
	}

	@Override
	default Map<K, CAS<V>> gets(Collection<K> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return Collections.emptyMap();
		}

		Codec<K, K> keyFomatter = getKeyFomatter();
		Map<K, CAS<V>> map = getSourceOperations().gets(keyFomatter == null ? keys : keyFomatter.encodeAll(keys));
		if (CollectionUtils.isEmpty(map)) {
			return Collections.emptyMap();
		}

		Codec<V, V> valueFomatter = getValueFomatter();
		Map<K, CAS<V>> targetMap = new LinkedHashMap<K, CAS<V>>(map.size());
		for (Entry<K, CAS<V>> entry : map.entrySet()) {
			CAS<V> value = entry.getValue();
			if (value == null) {
				continue;
			}

			targetMap.put(keyFomatter == null ? entry.getKey() : keyFomatter.decode(entry.getKey()),
					valueFomatter == null ? value : value.convert((e) -> valueFomatter.decode(e)));
		}
		return targetMap;
	}

	@Override
	default boolean cas(K key, V value, long cas) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<V, V> valueFomatter = getValueFomatter();
		return cas(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), cas);
	}
}
