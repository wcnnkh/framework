package io.basc.framework.redis.convert;

import io.basc.framework.codec.Codec;
import io.basc.framework.redis.RedisClient;
import io.basc.framework.redis.RedisCodecAccess;

public class DefaultConvertibleRedisClient<C extends RedisClient<SK, SV>, SK, K, SV, V>
		extends RedisCodecAccess<SK, K, SV, V> implements ConvertibleRedisClient<SK, K, SV, V> {
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
