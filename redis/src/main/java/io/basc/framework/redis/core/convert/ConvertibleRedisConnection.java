package io.basc.framework.redis.core.convert;

import io.basc.framework.codec.Codec;
import io.basc.framework.redis.core.RedisCommands;
import io.basc.framework.redis.core.RedisConnection;

public class ConvertibleRedisConnection<TK, TV, K, V> extends ConvertibleRedisCommands<TK, TV, K, V>
		implements RedisConnection<K, V> {
	private final RedisConnection<TK, TV> redisConnection;

	public ConvertibleRedisConnection(RedisConnection<TK, TV> redisConnection, Codec<K, TK> keyCodec,
			Codec<V, TV> valueCodec) {
		super(keyCodec, valueCodec);
		this.redisConnection = redisConnection;
	}

	@Override
	public boolean isQueueing() {
		return redisConnection.isQueueing();
	}

	@Override
	public boolean isPipelined() {
		return redisConnection.isPipelined();
	}

	@Override
	public void close() {
		redisConnection.close();
	}

	@Override
	protected RedisCommands<TK, TV> getTargetRedisCommands() {
		return redisConnection;
	}

}
