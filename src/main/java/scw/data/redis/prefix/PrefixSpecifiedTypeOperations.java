package scw.data.redis.prefix;

import scw.data.redis.RedisOperations;
import scw.data.redis.operations.SpecifiedTypeOperations;
import scw.io.serializer.Serializer;

public final class PrefixSpecifiedTypeOperations<T> extends PrefixRedisOperationsWrapper<T> {

	public PrefixSpecifiedTypeOperations(RedisOperations<String, T> stringRedisOperations, String keyPrefix) {
		super(stringRedisOperations, keyPrefix);
	}

	public PrefixSpecifiedTypeOperations(Class<T> type, RedisOperations<byte[], byte[]> binaryOperations,
			RedisOperations<String, String> stringOperations, Serializer serializer, String keyPrefix) {
		this(new SpecifiedTypeOperations<T>(type, binaryOperations, stringOperations, serializer), keyPrefix);
	}
}
