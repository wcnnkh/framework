package io.basc.framework.data.kv;

public interface KeyCasOperations<K> extends KeyOperations<K> {
	boolean delete(K key, long cas);
}
