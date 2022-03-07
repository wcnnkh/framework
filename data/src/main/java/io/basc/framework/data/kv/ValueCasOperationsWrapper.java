package io.basc.framework.data.kv;

import io.basc.framework.codec.Encoder;

public interface ValueCasOperationsWrapper<K, V> extends ValueCasOperations<K, V>, ValueOperationsWrapper<K, V> {
	ValueCasOperations<K, V> getSourceOperations();

	@Override
	default boolean cas(K key, V value, long cas) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<V, V> valueFomatter = getValueFomatter();
		return cas(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), cas);
	}
}
