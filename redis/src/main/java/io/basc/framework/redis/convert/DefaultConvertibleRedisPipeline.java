package io.basc.framework.redis.convert;

import io.basc.framework.codec.Codec;
import io.basc.framework.redis.RedisPipeline;

public class DefaultConvertibleRedisPipeline<C extends RedisPipeline<SK, SV>, SK, K, SV, V> extends
		DefaultConvertibleRedisPipelineCommands<C, SK, K, SV, V> implements ConvertibleRedisPipeline<SK, K, SV, V> {

	public DefaultConvertibleRedisPipeline(C commands, Codec<K, SK> keyCodec, Codec<V, SV> valueCodec) {
		super(commands, keyCodec, valueCodec);
	}

	@Override
	public RedisPipeline<SK, SV> getSourceRedisPipeline() {
		return commands;
	}
}
