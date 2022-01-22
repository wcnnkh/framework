package io.basc.framework.redis.convert;

import io.basc.framework.redis.RedisCommands;
import io.basc.framework.redis.RedisConnection;
import io.basc.framework.redis.RedisPipeline;

public interface ConvertibleRedisConnection<SK, K, SV, V>
		extends ConvertibleRedisCommands<SK, K, SV, V>, RedisConnection<K, V> {

	RedisConnection<SK, SV> getSourceConnection();

	@Override
	default RedisCommands<SK, SV> getSourceRedisCommands() {
		return getSourceConnection();
	}

	@Override
	default boolean isQueueing() {
		return getSourceConnection().isQueueing();
	}

	@Override
	default boolean isPipelined() {
		return getSourceConnection().isPipelined();
	}

	@Override
	default void close() {
		getSourceConnection().close();
	}

	@Override
	default boolean isClosed() {
		return getSourceConnection().isClosed();
	}

	@Override
	default RedisPipeline<K, V> pipelined() {
		RedisPipeline<SK, SV> pipeline = getSourceConnection().pipelined();
		return new DefaultConvertibleRedisPipeline<>(pipeline, getKeyCodec(), getValueCodec());
	}
}
