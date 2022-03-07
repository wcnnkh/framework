package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;

public interface TemporaryKeyValueOperationsWrapper<K, V> extends TemporaryKeyValueOperations<K, V>,
		TemporaryKeyOperationsWrapper<K>, TemporaryValueOperationsWrapper<K, V>, KeyValueOperationsWrapper<K, V> {

	TemporaryKeyValueOperations<K, V> getSourceOperations();

	@Override
	default V getAndTouch(K key, long exp, TimeUnit expUnit) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		V value = getAndTouch(keyFomatter == null ? key : keyFomatter.encode(key), exp, expUnit);
		return valueFomatter == null ? value : valueFomatter.decode(value);
	}
}
