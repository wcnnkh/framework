package io.basc.framework.redis.core;

import io.basc.framework.lang.Nullable;

public interface MessageListener<K, V> {

	/**
	 * Callback for processing received objects through Redis.
	 *
	 * @param message message must not be {@literal null}.
	 * @param pattern pattern matching the channel (if specified) - can be
	 *                {@literal null}.
	 */
	void onMessage(Message<K, V> message, @Nullable K pattern);
}