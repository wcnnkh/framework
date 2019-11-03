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

	public long incrAndGet(CountLimitConfig countLimitConfig) {
		return cacheService.incr(countLimitConfig.getName(), 1, 1,
				(int) countLimitConfig.getPeriod(TimeUnit.SECONDS));
	}
}
