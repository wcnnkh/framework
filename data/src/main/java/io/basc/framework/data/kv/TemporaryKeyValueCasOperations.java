package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

public interface TemporaryKeyValueCasOperations<K, V>
		extends KeyValueCasOperations<K, V>, TemporaryKeyValueOperations<K, V>, TemporaryKeyCasOperations<K> {
	boolean cas(K key, V value, long cas, long exp, TimeUnit expUnit);
}
