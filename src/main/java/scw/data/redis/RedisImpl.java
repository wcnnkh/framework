package scw.data.redis;

import scw.data.cas.CASOperations;
import scw.data.redis.operations.RedisCASOperations;
import scw.data.redis.prefix.PrefixObjectOperations;
import scw.data.redis.prefix.PrefixSpecifiedTypeOperations;
import scw.io.serializer.Serializer;

public class RedisImpl implements Redis {
	private final RedisOperations<byte[], byte[]> binaryOperations;
	private final RedisOperations<String, String> stringOperations;
	private final RedisOperations<String, Object> objectOperations;
	private final CASOperations casOperations;
	private final Serializer serializer;
	private final String keyPrefix;

	public RedisImpl(RedisOperations<byte[], byte[]> binaryOperations,
			RedisOperations<String, String> stringOperations, String keyPrefix, String charsetName,
			Serializer serializer) {
		this.binaryOperations = binaryOperations;
		this.stringOperations = stringOperations;
		this.serializer = serializer;
		this.keyPrefix = keyPrefix;
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

	public <T> RedisOperations<String, T> getSpecifiedTypeOperations(Class<T> type) {
		return new PrefixSpecifiedTypeOperations<T>(type, binaryOperations, stringOperations, serializer, keyPrefix);
	}

	public CASOperations getCASOperations() {
		return casOperations;
	}

}
