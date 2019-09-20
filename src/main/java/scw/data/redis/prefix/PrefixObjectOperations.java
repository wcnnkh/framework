package scw.data.redis.prefix;

import scw.data.redis.RedisOperations;
import scw.data.redis.operations.ObjectOperations;
import scw.io.serializer.Serializer;

public final class PrefixObjectOperations extends PrefixRedisOperationsWrapper<Object> {

	public PrefixObjectOperations(RedisOperations<String, Object> stringRedisOperations, String keyPrefix) {
		super(stringRedisOperations, keyPrefix);
	}

	public PrefixObjectOperations(RedisOperations<byte[], byte[]> redisOperations, String charsetName,
			Serializer serializer, String keyPrefix) {
		super(new ObjectOperations(redisOperations, charsetName, serializer), keyPrefix);
	}
}
