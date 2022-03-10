package io.basc.framework.data.kv;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.Encoder;

public interface StorageWrapper<K, V> extends Storage<K, V>, KeyValueOperationsWrapper<K, V> {
	@Override
	Storage<K, V> getSourceOperations();

	@Override
	default Long ttl(K key) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		return getSourceOperations().ttl(keyFomatter == null ? key : keyFomatter.encode(key));
	}

	@Override
	default boolean touch(K key) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		return getSourceOperations().touch(keyFomatter == null ? key : keyFomatter.encode(key));
	}

	@Override
	default V getAndTouch(K key) throws UnsupportedOperationException {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		V value = getSourceOperations().getAndTouch(keyFomatter == null ? key : keyFomatter.encode(key));
		return valueFomatter == null ? value : valueFomatter.decode(value);
	}
}
