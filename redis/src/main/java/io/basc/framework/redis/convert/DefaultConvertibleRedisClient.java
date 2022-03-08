package io.basc.framework.redis.convert;

import io.basc.framework.codec.Codec;
import io.basc.framework.redis.RedisClient;

public class DefaultConvertibleRedisClient<C extends RedisClient<SK, SV>, SK, K, SV, V, T extends DefaultConvertibleRedisClient<C, SK, K, SV, V, T>>
		extends RedisCodecAccess<SK, K, SV, V, T> implements ConvertibleRedisClient<SK, K, SV, V> {
	protected final C client;

	public DefaultConvertibleRedisClient(C client, Codec<K, SK> keyCodec, Codec<V, SV> valueCodec) {
		super(keyCodec, valueCodec);
		this.client = client;
	}

	@Override
	public RedisClient<SK, SV> getSourceRedisClient() {
		return client;
	}
}
