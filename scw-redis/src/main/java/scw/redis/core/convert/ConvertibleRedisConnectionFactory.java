package scw.redis.core.convert;

import scw.codec.Codec;
import scw.redis.core.RedisConnection;
import scw.redis.core.RedisConnectionFactory;

public class ConvertibleRedisConnectionFactory<TK, TV, K, V> implements RedisConnectionFactory<K, V> {
	private final Codec<K, TK> keyCodec;
	private final Codec<V, TV> valueCodec;
	private final RedisConnectionFactory<TK, TV> targetConnectionFactory;

	public ConvertibleRedisConnectionFactory(RedisConnectionFactory<TK, TV> targetConnectionFactory,
			Codec<K, TK> keyCodec, Codec<V, TV> valueCodec) {
		this.targetConnectionFactory = targetConnectionFactory;
		this.keyCodec = keyCodec;
		this.valueCodec = valueCodec;
	}

	@Override
	public RedisConnection<K, V> getConnection() {
		RedisConnection<TK, TV> connection = targetConnectionFactory.getConnection();
		return new ConvertibleRedisConnection<TK, TV, K, V>(connection, keyCodec, valueCodec);
	}

	public Codec<K, TK> getKeyCodec() {
		return keyCodec;
	}

	public Codec<V, TV> getValueCodec() {
		return valueCodec;
	}

	public RedisConnectionFactory<TK, TV> getTargetConnectionFactory() {
		return targetConnectionFactory;
	}
}
