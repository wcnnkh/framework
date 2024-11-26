package io.basc.framework.redis.convert;

import io.basc.framework.redis.RedisConnection;
import io.basc.framework.util.codec.Codec;

public class DefaultConvertibleRedisConnection<C extends RedisConnection<SK, SV>, SK, K, SV, V, T extends DefaultConvertibleRedisConnection<C, SK, K, SV, V, T>>
		extends RedisCodecAccess<SK, K, SV, V, T> implements ConvertibleRedisConnection<SK, K, SV, V> {
	protected final C connection;

	public DefaultConvertibleRedisConnection(C connection, Codec<K, SK> keyCodec, Codec<V, SV> valueCodec) {
		super(keyCodec, valueCodec);
		this.connection = connection;
	}

	@Override
	public RedisConnection<SK, SV> getSourceConnection() {
		return connection;
	}
}
