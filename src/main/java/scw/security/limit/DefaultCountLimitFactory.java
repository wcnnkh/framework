package scw.security.limit;

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

	public CountLimit getCountLimit(CountLimitConfig countLimitConfig) {
		return new DefaultCountLimit(countLimitConfig, cacheService);
	}

}
