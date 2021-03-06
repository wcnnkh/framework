package scw.redis;

import scw.codec.Codec;
import scw.data.AbstractDataTempleteWrapper;
import scw.data.DataTemplete;
import scw.data.cas.CASOperations;
import scw.io.Serializer;

public class RedisImpl extends AbstractDataTempleteWrapper implements Redis {
	private final RedisOperations<byte[], byte[]> binaryOperations;
	private final RedisOperations<String, String> stringOperations;
	private final RedisOperations<String, Object> objectOperations;
	private final CASOperations casOperations;
	private final DataTemplete dataTemplete;

	public RedisImpl(RedisOperations<byte[], byte[]> binaryOperations, RedisOperations<String, String> stringOperations,
			Codec<String, byte[]> codec, Serializer serializer) {
		this.binaryOperations = binaryOperations;
		this.stringOperations = stringOperations;
		this.objectOperations = new ObjectOperations(binaryOperations, serializer, codec);
		this.casOperations = new RedisCASOperations(objectOperations, serializer, codec);
		this.dataTemplete = new RedisDataTemplete(this);
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
	public DataTemplete getDataTemplete() {
		return dataTemplete;
	}
}
