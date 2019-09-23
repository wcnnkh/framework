package scw.security.login;

import scw.beans.annotation.Bean;
import scw.core.annotation.NotRequire;
import scw.core.annotation.ParameterName;
import scw.core.annotation.ParameterValue;
import scw.data.cache.MemcachedTemporaryCache;
import scw.data.cache.RedisTemporaryCache;
import scw.data.cache.TemporaryCache;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;

@Bean(proxy=false)
public class DefaultLoginFactory<T> extends AbstractLoginFactory<T> {
	private final String prefix;

	public DefaultLoginFactory(TemporaryCache temporaryCache,
			@ParameterName("login-factory.exp") @ParameterValue(7 * 24 * 60 * 60 + "") int exp,
			@ParameterName("login-factory.prefix") @NotRequire String prefix) {
		super(temporaryCache, exp);
		this.prefix = prefix;
	}

	public DefaultLoginFactory(Memcached memcached, int exp, String prefix) {
		this(new MemcachedTemporaryCache(memcached), exp, prefix);
	}

	public DefaultLoginFactory(Redis redis, int exp, String prefix) {
		this(new RedisTemporaryCache(redis), exp, prefix);
	}

	@Override
	protected String formatKey(Object key) {
		return prefix == null ? key.toString() : (prefix.concat(key.toString()));
	}
}
