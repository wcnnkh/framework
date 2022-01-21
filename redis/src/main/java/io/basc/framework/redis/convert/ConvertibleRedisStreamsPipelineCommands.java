package io.basc.framework.redis.convert;

import java.util.List;

import io.basc.framework.redis.ClaimArgs;
import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisStreamsPipelineCommands;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisStreamsPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisStreamsPipelineCommands<K, V> {

	RedisStreamsPipelineCommands<SK, SV> getSourceRedisStreamsCommands();

	@Override
	default RedisResponse<Long> xack(K key, K group, K... ids) {
		return getSourceRedisStreamsCommands().xack(getKeyCodec().encode(key), getKeyCodec().encode(group),
				getKeyCodec().encode(ids));
	}

	@Override
	default RedisResponse<List<V>> xclaim(K key, K group, K consumer, long minIdleTime, ClaimArgs args, K... ids) {
		return getSourceRedisStreamsCommands().xclaim(getKeyCodec().encode(key), getKeyCodec().encode(group),
				getKeyCodec().encode(consumer), minIdleTime, args, getKeyCodec().encode(ids))
				.map((values) -> getValueCodec().decode(values));
	}

	@Override
	default RedisResponse<Long> xdel(K key, K... ids) {
		return getSourceRedisStreamsCommands().xdel(getKeyCodec().encode(key), getKeyCodec().encode(ids));
	}
}
