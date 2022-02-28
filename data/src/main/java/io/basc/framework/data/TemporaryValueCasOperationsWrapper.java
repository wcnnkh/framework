package io.basc.framework.data;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;

public interface TemporaryValueCasOperationsWrapper<K, V> extends TemporaryValueCasOperations<K, V>,
		TemporaryValueOperationsWrapper<K, V>, ValueCasOperationsWrapper<K, V> {

	TemporaryValueCasOperations<K, V> getSourceOperations();

	@Override
	default boolean cas(K key, V value, long cas, long exp, TimeUnit expUnit) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().cas(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), cas, exp, expUnit);
	}

	@Override
	default boolean cas(K key, V value, long cas) {
		return ValueCasOperationsWrapper.super.cas(key, value, cas);
	}
}
