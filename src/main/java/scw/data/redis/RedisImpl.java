package scw.data.redis;

import scw.data.cas.CASOperations;
import scw.data.redis.operations.RedisCASOperations;
import scw.data.redis.prefix.PrefixObjectOperations;
import scw.io.serializer.Serializer;

public class RedisImpl implements Redis {
	private final RedisOperations<byte[], byte[]> binaryOperations;
	private final RedisOperations<String, String> stringOperations;
	private final RedisOperations<String, Object> objectOperations;
	private final CASOperations casOperations;

	public RedisImpl(RedisOperations<byte[], byte[]> binaryOperations,
			RedisOperations<String, String> stringOperations, String keyPrefix, String charsetName,
			Serializer serializer) {
		this.binaryOperations = binaryOperations;
		this.stringOperations = stringOperations;
		this.objectOperations = new PrefixObjectOperations(binaryOperations, charsetName, serializer, keyPrefix);
		this.casOperations = new RedisCASOperations(objectOperations, charsetName, serializer);
	}

	public RedisOperations<String, String> getStringOperations() {
		return stringOperations;
	}

	public RedisOperations<byte[], byte[]> getBinaryOperations() {
		return binaryOperations;
	}

	public RedisOperations<String, Object> getObjectOperations() {
		return objectOperations;
	}

	public CASOperations getCASOperations() {
		return casOperations;
	}

}
