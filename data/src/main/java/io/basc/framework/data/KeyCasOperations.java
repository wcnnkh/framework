package io.basc.framework.data;

public interface KeyCasOperations<K> extends KeyOperations<K> {
	boolean delete(K key, long cas);
}
