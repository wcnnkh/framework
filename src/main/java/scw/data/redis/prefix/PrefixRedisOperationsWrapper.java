package scw.data.redis.prefix;

import scw.data.redis.RedisOperations;

public class PrefixRedisOperationsWrapper<V> extends AbstractPrefixRedisOperationsWrapper<V> {
	private final RedisOperations<String, V> stringRedisOperations;
	private final String keyPrefix;

	public PrefixRedisOperationsWrapper(RedisOperations<String, V> stringRedisOperations, String keyPrefix) {
		this.stringRedisOperations = stringRedisOperations;
		this.keyPrefix = keyPrefix;
	}

	@Override
	public final String getKeyPrefix() {
		return keyPrefix;
	}

	@Override
	public final RedisOperations<String, V> getStringOperations() {
		return stringRedisOperations;
	}

}
