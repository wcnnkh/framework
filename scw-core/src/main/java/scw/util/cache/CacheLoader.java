package scw.util.cache;

public interface CacheLoader<K, V> {
	V loader(K key) throws Exception;
}
