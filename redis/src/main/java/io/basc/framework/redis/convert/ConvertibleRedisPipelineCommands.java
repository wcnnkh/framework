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

	RedisPipelineCommands<SK, SV> getSourceRedisCommands();

	@Override
	default RedisGeoPipelineCommands<SK, SV> getSourceRedisGeoCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisHashesPipelineCommands<SK, SV> getSourceRedisHashesCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisHyperloglogPipelineCommands<SK, SV> getSourceRedisHyperloglogCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisKeysPipelineCommands<SK, SV> getSourceRedisKeysCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisListsPipelineCommands<SK, SV> getSourceRedisListsCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisScriptingPipelineCommands<SK, SV> getSourceRedisScriptingCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisSetsPipelineCommands<SK, SV> getSourceRedisSetsCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisSortedSetsPipelineCommands<SK, SV> getSourceRedisSortedSetsCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisStreamsPipelineCommands<SK, SV> getSourceRedisStreamsCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisStringPipelineCommands<SK, SV> getSourceRedisStringCommands() {
		return getSourceRedisCommands();
	}
}
