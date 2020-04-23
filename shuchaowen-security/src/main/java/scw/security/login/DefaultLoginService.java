package scw.security.login;

import scw.beans.annotation.Bean;
import scw.core.Constants;
import scw.core.parameter.annotation.DefaultValue;
import scw.core.parameter.annotation.ParameterName;
import scw.data.MemcachedDataTemplete;
import scw.data.RedisDataTemplete;
import scw.data.TemporaryCache;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;

@Bean(proxy = false)
public class DefaultLoginService<T> extends AbstractLoginService<T> {
	private final String prefix;

	public DefaultLoginService(TemporaryCache temporaryCache,
			@ParameterName("login-factory.exp") @DefaultValue(7 * 24 * 60 * 60 + "") int exp) {
		this(temporaryCache, exp, Constants.DEFAULT_PREFIX);
	}

	public DefaultLoginService(TemporaryCache temporaryCache, int exp, String prefix) {
		super(temporaryCache, exp);
		this.prefix = prefix;
	}

	public DefaultLoginService(Memcached memcached, int exp, String prefix) {
		this(new MemcachedDataTemplete(memcached), exp, prefix);
	}

	public DefaultLoginService(Redis redis, int exp, String prefix) {
		this(new RedisDataTemplete(redis), exp, prefix);
	}

	@Override
	protected String formatUid(T uid) {
		return prefix == null ? uid.toString() : (prefix.concat(uid.toString()));
	}
}
