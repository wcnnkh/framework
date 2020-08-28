package scw.util.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import scw.util.ConcurrentReferenceHashMap;
import scw.util.task.OnlyExecuteOnceCallable;

public class ConcurrentMapCacheOperations<K, V> extends
		AbstractCacheOperations<K, V> {
	private volatile ConcurrentMap<K, OnlyExecuteOnceCallable<V>> cacheMap;

	public ConcurrentMapCacheOperations(boolean useRefreenceCache) {
		this.cacheMap = useRefreenceCache ? new ConcurrentReferenceHashMap<K, OnlyExecuteOnceCallable<V>>()
				: new ConcurrentHashMap<K, OnlyExecuteOnceCallable<V>>();
	}

	public V get(K key, Callable<V> callable) throws Exception {
		Callable<V> cache = cacheMap.get(key);
		if (cache == null) {
			OnlyExecuteOnceCallable<V> use = new OnlyExecuteOnceCallable<V>(
					callable);
			Callable<V> old = cacheMap.putIfAbsent(key, use);
			cache = old == null ? use : old;
		}
		return cache.call();
	}

	@Override
	protected Callable<V> getCallable(K key) {
		return cacheMap.get(key);
	}

}
