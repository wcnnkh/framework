package scw.data.redis;

import java.io.UnsupportedEncodingException;

import scw.io.serializer.Serializer;

final class ObjectOperations extends AbstractObjectOperations {
	private final AbstractRedis redis;
	private final String charsetName;

	public ObjectOperations(AbstractRedis redis, String charsetName) {
		this.redis = redis;
		this.charsetName = charsetName;
	}

	@Override
	protected RedisOperations<byte[], byte[]> getBinaryOperations() {
		return redis.getBinaryOperations();
	}

	@Override
	public Serializer getSerializer() {
		return redis.getSerializer();
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
