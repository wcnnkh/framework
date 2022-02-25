package io.basc.framework.data.cas;

import io.basc.framework.data.kv.ValueOperations;

public interface CasValueOperations<K, V> extends ValueOperations<K, V> {
	boolean cas(K key, V value, long cas);
}
