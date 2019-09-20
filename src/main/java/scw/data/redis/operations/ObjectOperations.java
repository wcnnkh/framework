package scw.data.redis.operations;

import java.io.UnsupportedEncodingException;

import scw.data.redis.RedisOperations;
import scw.io.serializer.Serializer;

public final class ObjectOperations extends AbstractObjectOperations {
	private final RedisOperations<byte[], byte[]> redisOperations;
	private final String charsetName;
	private final Serializer serializer;

	public ObjectOperations(RedisOperations<byte[], byte[]> redisOperations, String charsetName,
			Serializer serializer) {
		this.redisOperations = redisOperations;
		this.charsetName = charsetName;
		this.serializer = serializer;
	}

	@Override
	public RedisOperations<byte[], byte[]> getBinaryOperations() {
		return redisOperations;
	}

	@Override
	public Serializer getSerializer() {
		return serializer;
	}

	@Override
	public byte[] string2bytes(String key) {
		try {
			return key.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String bytes2string(byte[] bytes) {
		try {
			return new String(bytes, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
