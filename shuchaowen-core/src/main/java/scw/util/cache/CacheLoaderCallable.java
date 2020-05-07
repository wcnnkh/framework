package scw.util.cache;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class CacheLoaderCallable<K, V> implements Callable<V>, Serializable{
	private static final long serialVersionUID = 1L;
	private final CacheLoader<K, V> cacheLoader;
	private final K key;

	public CacheLoaderCallable(K key, CacheLoader<K, V> cacheLoader) {
		this.cacheLoader = cacheLoader;
		this.key = key;
	}

	public V call() throws Exception {
		return cacheLoader.loader(key);
	}

	public CacheLoader<K, V> getCacheLoader() {
		return cacheLoader;
	}

	public K getKey() {
		return key;
	}
}
