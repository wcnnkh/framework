package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.Encoder;

public interface TemporaryKeyValueOperationsWrapper<K, V>
		extends TemporaryKeyValueOperations<K, V>, TemporaryKeyOperationsWrapper<K>, KeyValueOperationsWrapper<K, V> {

	TemporaryKeyValueOperations<K, V> getSourceOperations();

	@Override
	default V getAndTouch(K key, long exp, TimeUnit expUnit) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		V value = getAndTouch(keyFomatter == null ? key : keyFomatter.encode(key), exp, expUnit);
		return valueFomatter == null ? value : valueFomatter.decode(value);
	}

	@Override
	default void set(K key, V value, long exp, TimeUnit expUnit) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<V, V> valueFomatter = getValueFomatter();
		getSourceOperations().set(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, V value, long exp, TimeUnit expUnit) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfAbsent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, V value, long exp, TimeUnit expUnit) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfPresent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), exp, expUnit);
	}
}
