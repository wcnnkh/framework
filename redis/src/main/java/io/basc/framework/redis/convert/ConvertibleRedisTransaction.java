package io.basc.framework.redis.convert;

import io.basc.framework.redis.RedisPipelineCommands;
import io.basc.framework.redis.RedisTransaction;
import io.basc.framework.redis.RedisTransactionsCommands;

public interface ConvertibleRedisTransaction<SK, K, SV, V> extends ConvertibleRedisPipelineCommands<SK, K, SV, V>,
		ConvertibleRedisTransactionsCommands<SK, K, SV, V>, RedisTransaction<K, V> {

	RedisTransaction<SK, SV> getSourceRedisTransaction();

	@Override
	default RedisPipelineCommands<SK, SV> getSourceRedisCommands() {
		return getSourceRedisTransaction();
	}

	@Override
	default RedisTransactionsCommands<SK, SV> getSourceRedisTransactionsCommands() {
		return getSourceRedisTransaction();
	}
}
