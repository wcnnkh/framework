package scw.security.login;

import scw.beans.annotation.Bean;
import scw.core.Constants;
import scw.core.annotation.DefaultValue;
import scw.core.annotation.ParameterName;
import scw.data.cache.CacheService;
import scw.data.cache.MemcachedCacheService;
import scw.data.cache.RedisCacheService;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;

@Bean(proxy = false)
public class DefaultLoginFactory<T> extends AbstractLoginFactory<T> {
	private final String prefix;

	public DefaultLoginFactory(CacheService cacheService,
			@ParameterName("login-factory.exp") @DefaultValue(7 * 24 * 60 * 60 + "") int exp) {
		this(cacheService, exp, Constants.DEFAULT_PREFIX);
	}

	public DefaultLoginFactory(CacheService cacheService, int exp, String prefix) {
		super(cacheService, exp);
		this.prefix = prefix;
	}

	public DefaultLoginFactory(Memcached memcached, int exp, String prefix) {
		this(new MemcachedCacheService(memcached), exp, prefix);
	}

	public DefaultLoginFactory(Redis redis, int exp, String prefix) {
		this(new RedisCacheService(redis), exp, prefix);
	}

	@Override
	protected String formatKey(Object key) {
		return prefix == null ? key.toString() : (prefix.concat(key.toString()));
	}
}
