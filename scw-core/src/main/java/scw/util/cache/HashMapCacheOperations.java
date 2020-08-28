package scw.util.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import scw.util.task.OnlyExecuteOnceCallable;

public class HashMapCacheOperations<K, V> extends AbstractCacheOperations<K, V> {
	private volatile Map<K, OnlyExecuteOnceCallable<V>> cacheMap = new HashMap<K, OnlyExecuteOnceCallable<V>>();

	public V get(K key, Callable<V> callable) throws Exception {
		Callable<V> cache = getCallable(key);
		if (cache == null) {
			synchronized (cacheMap) {
				cache = getCallable(key);
				if (cache == null) {
					cache = callable;
					cacheMap.put(key, new OnlyExecuteOnceCallable<V>(cache));
				}
			}
		}
		return cache.call();
	}

	@Override
	protected Callable<V> getCallable(K key) {
		return cacheMap.get(key);
	}
}