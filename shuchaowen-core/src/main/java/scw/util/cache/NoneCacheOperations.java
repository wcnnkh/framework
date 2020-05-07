package scw.util.cache;

import java.util.concurrent.Callable;

public class NoneCacheOperations<K, V> implements CacheOperations<K, V> {

	public V get(K key) throws Exception {
		return null;
	}

	public V get(K key, CacheLoader<K, V> loader) throws Exception {
		return loader.loader(key);
	}

	public V get(K key, Callable<V> callable) throws Exception {
		return callable.call();
	}

	public boolean isExist(K key) {
		return false;
	}

}
