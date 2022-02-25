package io.basc.framework.data.kv;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.CollectionUtils;

public interface KeyValueOperationsWrapper<K, V>
		extends KeyValueOperations<K, V>, KeyOperationsWrapper<K>, ValueOperationsWrapper<K, V> {

	KeyValueOperations<K, V> getSourceOperations();

	@Override
	default Codec<K, K> getKeyFomatter() {
		return ValueOperationsWrapper.super.getKeyFomatter();
	}

	@Override
	default V get(K key) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		V value = get(keyFomatter == null ? key : keyFomatter.encode(key));
		return valueFomatter == null ? value : valueFomatter.decode(value);
	}

	@Override
	default Map<K, V> get(Collection<K> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return Collections.emptyMap();
		}

		Codec<K, K> keyFomatter = getKeyFomatter();
		Map<K, V> map = getSourceOperations().get(keyFomatter == null ? keys : keyFomatter.encode(keys));
		if (CollectionUtils.isEmpty(map)) {
			return Collections.emptyMap();
		}

		Codec<V, V> valueFomatter = getValueFomatter();
		Map<K, V> targetMap = new LinkedHashMap<K, V>(map.size());
		for (Entry<K, V> entry : map.entrySet()) {
			targetMap.put(keyFomatter == null ? entry.getKey() : keyFomatter.decode(entry.getKey()),
					valueFomatter == null ? entry.getValue() : valueFomatter.decode(entry.getValue()));
		}
		return targetMap;
	}
}
