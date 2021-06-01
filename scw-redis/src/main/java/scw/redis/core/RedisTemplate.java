package scw.redis.core;

import scw.codec.Codec;
import scw.codec.support.CharsetCodec;
import scw.redis.core.convert.ConvertibleRedisConnection;

public class RedisTemplate implements RedisConnectionFactory<String, String> {
	private Codec<String, byte[]> keyCodec = CharsetCodec.DEFAULT;
	private Codec<String, byte[]> valueCodec = CharsetCodec.DEFAULT;
	private final RedisConnectionFactory<byte[], byte[]> targetConnectionFactory;

	public RedisTemplate(RedisConnectionFactory<byte[], byte[]> targetConnectionFactory) {
		this.targetConnectionFactory = targetConnectionFactory;
	}

	public final RedisConnectionFactory<byte[], byte[]> getTargetConnectionFactory() {
		return targetConnectionFactory;
	}

	@Override
	public RedisConnection<String, String> getRedisConnection() {
		RedisConnection<byte[], byte[]> connection = targetConnectionFactory.getRedisConnection();
		return new ConvertibleRedisConnection<byte[], byte[], String, String>(connection, keyCodec, valueCodec);
	}

	public final Codec<String, byte[]> getKeyCodec() {
		return keyCodec;
	}

	public void setKeyCodec(Codec<String, byte[]> keyCodec) {
		this.keyCodec = keyCodec;
	}

	public final Codec<String, byte[]> getValueCodec() {
		return valueCodec;
	}

	public void setValueCodec(Codec<String, byte[]> valueCodec) {
		this.valueCodec = valueCodec;
	}

}
