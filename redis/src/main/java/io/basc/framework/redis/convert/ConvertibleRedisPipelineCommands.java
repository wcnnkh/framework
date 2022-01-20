package io.basc.framework.redis.convert;

import io.basc.framework.redis.RedisGeoPipelineCommands;
import io.basc.framework.redis.RedisHashesPipelineCommands;
import io.basc.framework.redis.RedisHyperloglogPipelineCommands;
import io.basc.framework.redis.RedisKeysPipelineCommands;
import io.basc.framework.redis.RedisListsPipelineCommands;
import io.basc.framework.redis.RedisPipelineCommands;
import io.basc.framework.redis.RedisScriptingPipelineCommands;
import io.basc.framework.redis.RedisSetsPipelineCommands;
import io.basc.framework.redis.RedisSortedSetsPipelineCommands;
import io.basc.framework.redis.RedisStreamsPipelineCommands;
import io.basc.framework.redis.RedisStringPipelineCommands;

public interface ConvertibleRedisPipelineCommands<SK, K, SV, V>
		extends ConvertibleRedisGeoPipelineCommands<SK, K, SV, V>, ConvertibleRedisHashesPipelineCommands<SK, K, SV, V>,
		ConvertibleRedisHyperloglogPipelineCommands<SK, K, SV, V>, ConvertibleRedisKeysPipelineCommands<SK, K, SV, V>,
		ConvertibleRedisListsPipelineCommands<SK, K, SV, V>, ConvertibleRedisScriptingPipelineCommands<SK, K, SV, V>,
		ConvertibleRedisSetsPipelineCommands<SK, K, SV, V>, ConvertibleRedisSortedSetsPipelineCommands<SK, K, SV, V>,
		ConvertibleRedisStreamsPipelineCommands<SK, K, SV, V>, ConvertibleRedisStringPipelineCommands<SK, K, SV, V>,
		RedisPipelineCommands<K, V> {

	RedisPipelineCommands<SK, SV> getSourceRedisPipelineCommands();

	@Override
	default RedisGeoPipelineCommands<SK, SV> getSourceRedisGeoPipelineCommands() {
		return getSourceRedisPipelineCommands();
	}

	@Override
	default RedisHashesPipelineCommands<SK, SV> getSourceRedisHashesPipelineCommands() {
		return getSourceRedisPipelineCommands();
	}

	@Override
	default RedisHyperloglogPipelineCommands<SK, SV> getSourceRedisHyperloglogPipelineCommands() {
		return getSourceRedisPipelineCommands();
	}

	@Override
	default RedisKeysPipelineCommands<SK, SV> getSourceRedisKeysPipelineCommands() {
		return getSourceRedisPipelineCommands();
	}

	@Override
	default RedisListsPipelineCommands<SK, SV> getSourceRedisListsPipelineCommands() {
		return getSourceRedisPipelineCommands();
	}

	@Override
	default RedisScriptingPipelineCommands<SK, SV> getSourceRedisScriptingPipelineCommands() {
		return getSourceRedisPipelineCommands();
	}

	@Override
	default RedisSetsPipelineCommands<SK, SV> getSourceRedisSetsPipelineCommands() {
		return getSourceRedisPipelineCommands();
	}

	@Override
	default RedisSortedSetsPipelineCommands<SK, SV> getSourceRedisSortedSetsPipelineCommands() {
		return getSourceRedisPipelineCommands();
	}

	@Override
	default RedisStreamsPipelineCommands<SK, SV> getSourceRedisStreamsPipelineCommands() {
		return getSourceRedisPipelineCommands();
	}

	@Override
	default RedisStringPipelineCommands<SK, SV> getSourceRedisStringPipelineCommands() {
		return getSourceRedisPipelineCommands();
	}
}
