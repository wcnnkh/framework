package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

public interface TemporaryValueCasOperations<K, V> extends ValueCasOperations<K, V>, TemporaryValueOperations<K, V> {
	@Override
	default boolean cas(K key, V value, long cas) {
		return cas(key, value, cas, 0, TimeUnit.MILLISECONDS);
	}

	boolean cas(K key, V value, long cas, long exp, TimeUnit expUnit);
}
