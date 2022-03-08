package io.basc.framework.redis.convert;

import io.basc.framework.codec.Codec;
import io.basc.framework.redis.RedisTransaction;

public class DefaultConvertibleRedisTransactionsCommands<C extends RedisTransaction<SK, SV>, SK, K, SV, V, T extends DefaultConvertibleRedisTransactionsCommands<C, SK, K, SV, V, T>>
		extends DefaultConvertibleRedisPipelineCommands<C, SK, K, SV, V, T>
		implements ConvertibleRedisTransaction<SK, K, SV, V> {

	public DefaultConvertibleRedisTransactionsCommands(C commands, Codec<K, SK> keyCodec, Codec<V, SV> valueCodec) {
		super(commands, keyCodec, valueCodec);
	}

	@Override
	public RedisTransaction<SK, SV> getSourceRedisTransaction() {
		return commands;
	}
}
