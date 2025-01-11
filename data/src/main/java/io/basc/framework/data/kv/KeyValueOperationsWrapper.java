package io.basc.framework.data.kv;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.Encoder;
import io.basc.framework.util.collections.CollectionUtils;

public interface KeyValueOperationsWrapper<K, V> extends KeyValueOperations<K, V>, KeyOperationsWrapper<K> {

	KeyValueOperations<K, V> getSourceOperations();

	@Override
	default Codec<K, K> getKeyFomatter() {
		return null;
	}

	default Codec<V, V> getValueFomatter() {
		return null;
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
		Map<K, V> map = getSourceOperations().get(keyFomatter == null ? keys : keyFomatter.encodeAll(keys));
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

	@Override
	default void set(K key, V value) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<V, V> valueFomatter = getValueFomatter();
		getSourceOperations().set(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value));
	}

	@Override
	default boolean setIfAbsent(K key, V value) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfAbsent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value));
	}

	@Override
	default boolean setIfPresent(K key, V value) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfPresent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value));
	}
}
