package io.basc.framework.data.cas;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;
import io.basc.framework.data.kv.TemporaryValueOperationsWrapper;

public interface CasTemporaryValueOperationsWrapper<K, V> extends CasTemporaryValueOperations<K, V>,
		TemporaryValueOperationsWrapper<K, V>, CasValueOperationsWrapper<K, V> {

	CasTemporaryValueOperations<K, V> getSourceOperations();

	@Override
	default boolean cas(K key, V value, long cas, long exp, TimeUnit expUnit) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().cas(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), cas, exp, expUnit);
	}

	@Override
	default boolean cas(K key, V value, long cas) {
		return CasValueOperationsWrapper.super.cas(key, value, cas);
	}
}
