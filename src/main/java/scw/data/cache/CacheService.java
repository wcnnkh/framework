package scw.data.cache;

import java.util.Collection;
import java.util.Map;

import scw.beans.annotation.AutoImpl;
import scw.data.cache.memory.MemoryCacheService;

@AutoImpl({ RedisCacheService.class, MemcachedCacheService.class, MemoryCacheService.class })
public interface CacheService {
	<T> T get(String key);

	<T> T getAndTouch(String key, int newExp);

	boolean set(String key, Object value);

	boolean set(String key, int exp, Object value);

	boolean add(String key, Object value);

	boolean add(String key, int exp, Object value);

	boolean touch(String key, int exp);

	<T> Map<String, T> get(Collection<String> keyCollections);

	boolean delete(String key);

	boolean isExist(String key);

	long incr(String key, long delta);

	long incr(String key, long delta, long initialValue);

	long incr(String key, long delta, long initialValue, int exp);

	long decr(String key, long delta);

	long decr(String key, long delta, long initialValue);

	long decr(String key, long delta, long initialValue, int exp);
}
