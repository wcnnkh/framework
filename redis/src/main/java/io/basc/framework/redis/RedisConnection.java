package io.basc.framework.redis;

public interface RedisConnection<K, V> extends RedisCommands<K, V>, AutoCloseable {
	boolean isQueueing();

	void close();

	boolean isClosed();

	boolean isPipelined();

	RedisPipeline<K, V> pipelined();
}
