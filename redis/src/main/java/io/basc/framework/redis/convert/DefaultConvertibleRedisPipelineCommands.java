package io.basc.framework.redis.convert;

import io.basc.framework.codec.Codec;
import io.basc.framework.redis.RedisPipelineCommands;

public class DefaultConvertibleRedisPipelineCommands<P extends RedisPipelineCommands<SK, SV>, SK, K, SV, V>
		extends RedisCodecAccess<SK, K, SV, V> implements ConvertibleRedisPipelineCommands<SK, K, SV, V> {
	protected final P commands;

	public DefaultConvertibleRedisPipelineCommands(P commands, Codec<K, SK> keyCodec, Codec<V, SV> valueCodec) {
		super(keyCodec, valueCodec);
		this.commands = commands;
	}

	@Override
	public P getSourceRedisCommands() {
		return commands;
	}
}
