package scw.data.cache;

import scw.beans.annotation.AutoImpl;
import scw.data.SimpleCacheService;
import scw.data.memory.MemoryCacheService;

@AutoImpl({ RedisCacheService.class, MemcachedCacheService.class, MemoryCacheService.class })
public interface CacheService extends SimpleCacheService{

	long incr(String key, long delta);

	long incr(String key, long delta, long initialValue);

	long incr(String key, long delta, long initialValue, int exp);

	long decr(String key, long delta);

	long decr(String key, long delta, long initialValue);

	long decr(String key, long delta, long initialValue, int exp);
}
