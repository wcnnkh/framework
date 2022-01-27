package io.basc.framework.redis.convert;

import io.basc.framework.redis.RedisCommands;
import io.basc.framework.redis.RedisConnectionCommands;
import io.basc.framework.redis.RedisGeoCommands;
import io.basc.framework.redis.RedisHashesCommands;
import io.basc.framework.redis.RedisHyperloglogCommands;
import io.basc.framework.redis.RedisKeysCommands;
import io.basc.framework.redis.RedisListsCommands;
import io.basc.framework.redis.RedisPubSubCommands;
import io.basc.framework.redis.RedisScriptingCommands;
import io.basc.framework.redis.RedisServerCommands;
import io.basc.framework.redis.RedisSetsCommands;
import io.basc.framework.redis.RedisSortedSetsCommands;
import io.basc.framework.redis.RedisStreamsCommands;
import io.basc.framework.redis.RedisStringCommands;
import io.basc.framework.redis.RedisTransactionsCommands;

public interface ConvertibleRedisCommands<SK, K, SV, V> extends ConvertibleRedisConnectionCommands<SK, K, SV, V>,
		ConvertibleRedisGeoCommands<SK, K, SV, V>, ConvertibleRedisHashesCommands<SK, K, SV, V>,
		ConvertibleRedisHyperloglogCommands<SK, K, SV, V>, ConvertibleRedisKeysCommands<SK, K, SV, V>,
		ConvertibleRedisListsCommands<SK, K, SV, V>, ConvertibleRedisScriptingCommands<SK, K, SV, V>,
		ConvertibleRedisPubSubCommands<SK, K, SV, V>, ConvertibleRedisSetsCommands<SK, K, SV, V>,
		ConvertibleRedisSortedSetsCommands<SK, K, SV, V>, ConvertibleRedisStreamsCommands<SK, K, SV, V>,
		ConvertibleRedisStringCommands<SK, K, SV, V>, ConvertibleRedisTransactionsCommands<SK, K, SV, V>,
		ConvertibleRedisServerCommands<SK, K, SV, V>, RedisCommands<K, V> {

	RedisCommands<SK, SV> getSourceRedisCommands();

	@Override
	default RedisConnectionCommands<SK, SV> getSourceRedisConnectionCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisGeoCommands<SK, SV> getSourceRedisGeoCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisHyperloglogCommands<SK, SV> getSourceRedisHyperloglogCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisKeysCommands<SK, SV> getSourceRedisKeysCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisListsCommands<SK, SV> getSourceRedisListsCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisPubSubCommands<SK, SV> getSourceRedisPubSubCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisScriptingCommands<SK, SV> getSourceRedisScriptingCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisSetsCommands<SK, SV> getSourceRedisSetsCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisSortedSetsCommands<SK, SV> getSourceRedisSortedSetsCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisStreamsCommands<SK, SV> getSourceRedisStreamsCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisStringCommands<SK, SV> getSourceRedisStringCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisTransactionsCommands<SK, SV> getSourceRedisTransactionsCommands() {
		return getSourceRedisCommands();
	}

	@Override
	default RedisHashesCommands<SK, SV> getSourceRedisHashesCommands() {
		return getSourceRedisCommands();
	}
	
	@Override
	default RedisServerCommands<SK, SV> getSourceRedisServerCommands() {
		return getSourceRedisCommands();
	}
}
