package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;

public interface TemporaryValueOperationsWrapper<K, V>
		extends TemporaryValueOperations<K, V>, ValueOperationsWrapper<K, V> {

	@Override
	TemporaryValueOperations<K, V> getSourceOperations();

	@Override
	default void set(K key, V value, long exp, TimeUnit expUnit) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		getSourceOperations().set(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, V value, long exp, TimeUnit expUnit) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfAbsent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, V value, long exp, TimeUnit expUnit) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfPresent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, V value) {
		return ValueOperationsWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, V value) {
		return ValueOperationsWrapper.super.setIfPresent(key, value);
	}

	@Override
	default void set(K key, V value) {
		ValueOperationsWrapper.super.set(key, value);
	}
}
