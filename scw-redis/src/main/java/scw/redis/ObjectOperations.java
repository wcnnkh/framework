package scw.redis;

import scw.codec.Codec;
import scw.io.Serializer;

public final class ObjectOperations extends AbstractRedisOperationsWrapper<String, byte[], Object, byte[]> {
	private final RedisOperations<byte[], byte[]> redisOperations;
	private final Codec<String, byte[]> codec;
	private final Serializer serializer;

	public ObjectOperations(RedisOperations<byte[], byte[]> redisOperations, Serializer serializer,
			Codec<String, byte[]> codec) {
		this.redisOperations = redisOperations;
		this.codec = codec;
		this.serializer = serializer;
	}

	@Override
	protected RedisOperations<byte[], byte[]> getRedisOperations() {
		return redisOperations;
	}

	@Override
	protected byte[] encodeKey(String key) {
		if (key == null) {
			return null;
		}

		return codec.encode(key);
	}

	@Override
	protected String decodeKey(byte[] key) {
		if (key == null) {
			return null;
		}

		return codec.decode(key);
	}

	@Override
	protected byte[] encodeValue(Object value) {
		if (value == null) {
			return null;
		}

		return serializer.serialize(value);
	}

	@Override
	protected Object decodeValue(byte[] value) {
		if (value == null) {
			return null;
		}

		try {
			return serializer.deserialize(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
