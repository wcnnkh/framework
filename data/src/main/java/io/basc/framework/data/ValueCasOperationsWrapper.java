package io.basc.framework.data;

import io.basc.framework.codec.Codec;

public interface ValueCasOperationsWrapper<K, V> extends ValueCasOperations<K, V>, ValueOperationsWrapper<K, V> {
	ValueCasOperations<K, V> getSourceOperations();

	@Override
	default boolean cas(K key, V value, long cas) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		return cas(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), cas);
	}
}
