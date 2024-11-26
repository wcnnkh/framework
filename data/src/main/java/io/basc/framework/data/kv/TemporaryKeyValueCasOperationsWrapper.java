package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.codec.Encoder;

public interface TemporaryKeyValueCasOperationsWrapper<K, V>
		extends TemporaryKeyValueCasOperations<K, V>, KeyValueCasOperationsWrapper<K, V>,
		TemporaryKeyCasOperationsWrapper<K>, TemporaryKeyValueOperationsWrapper<K, V> {

	@Override
	TemporaryKeyValueCasOperations<K, V> getSourceOperations();

	@Override
	default boolean cas(K key, V value, long cas, long exp, TimeUnit expUnit) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().cas(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), cas, exp, expUnit);
	}
}
