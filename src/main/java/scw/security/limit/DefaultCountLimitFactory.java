package scw.security.limit;

import java.util.concurrent.TimeUnit;

import scw.data.cache.CacheService;
import scw.data.cache.MemcachedCacheService;
import scw.data.cache.RedisCacheService;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;

public final class DefaultCountLimitFactory implements CountLimitFactory {
	private final CacheService cacheService;

	public DefaultCountLimitFactory(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	public DefaultCountLimitFactory(Memcached memcached) {
		this(new MemcachedCacheService(memcached));
	}

	public DefaultCountLimitFactory(Redis redis) {
		this(new RedisCacheService(redis));
	}

	public long incrAndGet(String name, long timeout, TimeUnit timeUnit) {
		return cacheService.incr(name, 1, 1, (int) timeUnit.toSeconds(timeout));
	}
}
