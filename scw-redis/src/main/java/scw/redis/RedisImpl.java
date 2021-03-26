package scw.redis;

import scw.codec.Codec;
import scw.data.AbstractDataOperationsWrapper;
import scw.data.DataOperations;
import scw.data.cas.CASOperations;
import scw.io.Serializer;

public class RedisImpl extends AbstractDataOperationsWrapper implements Redis {
	private final RedisOperations<byte[], byte[]> binaryOperations;
	private final RedisOperations<String, String> stringOperations;
	private final RedisOperations<String, Object> objectOperations;
	private final CASOperations casOperations;
	private final DataOperations dataOperations;

	public RedisImpl(RedisOperations<byte[], byte[]> binaryOperations, RedisOperations<String, String> stringOperations,
			Codec<String, byte[]> codec, Serializer serializer) {
		this.binaryOperations = binaryOperations;
		this.stringOperations = stringOperations;
		this.objectOperations = new ObjectOperations(binaryOperations, serializer, codec);
		this.casOperations = new RedisCASOperations(objectOperations, serializer, codec);
		this.dataOperations = new RedisDataOperations(this);
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

	@Override
	public DataOperations getDataOperations() {
		return dataOperations;
	}
}
