package scw.redis.connection;

import scw.lang.Nullable;

public interface MessageListener {

	/**
	 * Callback for processing received objects through Redis.
	 *
	 * @param message message must not be {@literal null}.
	 * @param pattern pattern matching the channel (if specified) - can be {@literal null}.
	 */
	void onMessage(Message message, @Nullable byte[] pattern);
}