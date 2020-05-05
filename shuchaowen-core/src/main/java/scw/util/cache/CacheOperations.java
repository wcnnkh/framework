package scw.util.cache;

import java.util.concurrent.Callable;


public interface CacheOperations<K, V> {
	V get(K key) throws Exception;
	
	V get(K key, CacheLoader<K, V> loader) throws Exception;
	
	V get(K key, Callable<V> callable) throws Exception;
	
	boolean isExist(K key);
}
