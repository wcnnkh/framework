package scw.data.cache;

public interface CacheFactory {
	<T> Cache<T> getCache(Class<T> type);
}
