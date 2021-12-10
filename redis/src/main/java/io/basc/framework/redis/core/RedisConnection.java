package io.basc.framework.redis.core;

public interface RedisConnection<K, V> extends RedisCommands<K, V>, AutoCloseable {
	/**
	 * Indicates whether the connection is in "queue"(or "MULTI") mode or not. When
	 * queueing, all commands are postponed until EXEC or DISCARD commands are
	 * issued. Since in queueing no results are returned, the connection will return
	 * NULL on all operations that interact with the data.
	 *
	 * @return true if the connection is in queue/MULTI mode, false otherwise
	 */
	boolean isQueueing();

	/**
	 * Indicates whether the connection is currently pipelined or not.
	 *
	 * @return true if the connection is pipelined, false otherwise
	 * @see #openPipeline()
	 * @see #isQueueing()
	 */
	boolean isPipelined();

	void close();
}
