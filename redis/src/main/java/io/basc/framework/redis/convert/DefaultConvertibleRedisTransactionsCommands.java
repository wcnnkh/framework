package io.basc.framework.redis.convert;

import io.basc.framework.redis.RedisTransaction;
import io.basc.framework.util.codec.Codec;

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
