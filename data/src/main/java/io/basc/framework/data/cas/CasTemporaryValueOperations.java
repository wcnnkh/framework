package io.basc.framework.data.cas;

import java.util.concurrent.TimeUnit;

import io.basc.framework.data.kv.TemporaryValueOperations;

public interface CasTemporaryValueOperations<K, V> extends CasValueOperations<K, V>, TemporaryValueOperations<K, V> {
	@Override
	default boolean cas(K key, V value, long cas) {
		return cas(key, value, cas, 0, TimeUnit.MILLISECONDS);
	}

	boolean cas(K key, V value, long cas, long exp, TimeUnit expUnit);
}
