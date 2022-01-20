package io.basc.framework.redis.convert;

import java.util.List;

import io.basc.framework.codec.Codec;
import io.basc.framework.redis.RedisCommands;
import io.basc.framework.redis.RedisConnection;

public class ConvertibleRedisConnection<SK, K, SV, V>
		implements ConvertibleRedisCommands<SK, K, SV, V>, RedisConnection<K, V> {
	private final RedisConnection<SK, SV> redisConnection;
	private final Codec<K, SK> keyCodec;
	private final Codec<V, SV> valueCodec;

	public ConvertibleRedisConnection(RedisConnection<SK, SV> redisConnection, Codec<K, SK> keyCodec,
			Codec<V, SV> valueCodec) {
		this.redisConnection = redisConnection;
		this.keyCodec = keyCodec;
		this.valueCodec = valueCodec;
	}

	@Override
	public RedisCommands<SK, SV> getSourceRedisCommands() {
		return redisConnection;
	}

	@Override
	public Codec<K, SK> getKeyCodec() {
		return keyCodec;
	}

	@Override
	public Codec<V, SV> getValueCodec() {
		return valueCodec;
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
	public boolean isClosed() {
		return redisConnection.isClosed();
	}

	@Override
	public void openPipeline() {
		redisConnection.openPipeline();
	}

	@Override
	public List<Object> closePipeline() {
		return redisConnection.closePipeline();
	}
}
