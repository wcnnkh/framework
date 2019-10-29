package scw.data.redis;

import scw.core.string.StringCodec;
import scw.io.serializer.Serializer;

public final class ObjectOperations extends AbstractRedisOperationsWrapper<String, byte[], Object, byte[]> {
	private final RedisOperations<byte[], byte[]> redisOperations;
	private final StringCodec stringCodec;
	private final Serializer serializer;

	public ObjectOperations(RedisOperations<byte[], byte[]> redisOperations, Serializer serializer,
			StringCodec stringCodec) {
		this.redisOperations = redisOperations;
		this.stringCodec = stringCodec;
		this.serializer = serializer;
	}

	@Override
	protected RedisOperations<byte[], byte[]> getRedisOperations() {
		return redisOperations;
	}

	@Override
	protected byte[] encodeKey(String key) {
		return stringCodec.encode(key);
	}

	@Override
	protected String decodeKey(byte[] key) {
		return stringCodec.decode(key);
	}

	@Override
	protected byte[] encodeValue(Object value) {
		return serializer.serialize(value);
	}

	@Override
	protected Object decodeValue(byte[] value) {
		return serializer.deserialize(value);
	}
}
