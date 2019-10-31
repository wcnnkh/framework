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
public class DefaultLoginService<T> extends AbstractLoginService<T> {
	private final String prefix;

	public DefaultLoginService(CacheService cacheService,
			@ParameterName("login-factory.exp") @DefaultValue(7 * 24 * 60 * 60 + "") int exp) {
		this(cacheService, exp, Constants.DEFAULT_PREFIX);
	}

	public DefaultLoginService(CacheService cacheService, int exp, String prefix) {
		super(cacheService, exp);
		this.prefix = prefix;
	}

	public DefaultLoginService(Memcached memcached, int exp, String prefix) {
		this(new MemcachedCacheService(memcached), exp, prefix);
	}

	public DefaultLoginService(Redis redis, int exp, String prefix) {
		this(new RedisCacheService(redis), exp, prefix);
	}

	@Override
	protected String formatUid(T uid) {
		return prefix == null ? uid.toString() : (prefix.concat(uid.toString()));
	}
}
