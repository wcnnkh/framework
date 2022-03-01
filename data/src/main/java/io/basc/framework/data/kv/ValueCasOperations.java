package io.basc.framework.data.kv;

public interface ValueCasOperations<K, V> extends ValueOperations<K, V> {
	boolean cas(K key, V value, long cas);
}
