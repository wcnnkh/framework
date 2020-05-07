package scw.util.cache;

import java.util.concurrent.Callable;


public abstract class AbstractCacheOperations<K, V> implements CacheOperations<K, V>{

	public V get(K key, CacheLoader<K, V> loader) throws Exception {
		return get(key, new CacheLoaderCallable<K, V>(key, loader));
	}
	
	protected abstract Callable<V> getCallable(K key);
	
	public V get(K key) throws Exception {
		Callable<V> callable = getCallable(key);
		return callable == null? null:callable.call();
	};
	
	public boolean isExist(K key) {
		return getCallable(key) != null;
	}
}
