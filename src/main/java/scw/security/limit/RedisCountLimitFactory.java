package scw.security.limit;

import scw.data.redis.Redis;

public final class RedisCountLimitFactory implements CountLimitFactory {
	private final Redis redis;

	public RedisCountLimitFactory(Redis redis) {
		this.redis = redis;
	}

	public CountLimit getCountLimit(CountLimitConfig countLimitConfig) {
		return new RedisCountLimit(countLimitConfig, redis);
	}

}
