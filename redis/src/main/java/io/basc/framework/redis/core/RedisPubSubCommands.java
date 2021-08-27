package io.basc.framework.redis.core;

import io.basc.framework.lang.Nullable;

@SuppressWarnings("unchecked")
public interface RedisPubSubCommands<K, V> {

	/**
	 * Indicates whether the current connection is subscribed (to at least one channel) or not.
	 *
	 * @return true if the connection is subscribed, false otherwise
	 */
	boolean isSubscribed();

	/**
	 * Returns the current subscription for this connection or null if the connection is not subscribed.
	 *
	 * @return the current subscription, {@literal null} if none is available.
	 */
	@Nullable
	Subscription<K, V> getSubscription();

	/**
	 * Publishes the given message to the given channel.
	 *
	 * @param channel the channel to publish to. Must not be {@literal null}.
	 * @param message message to publish. Must not be {@literal null}.
	 * @return the number of clients that received the message or {@literal null} when used in pipeline / transaction.
	 * @see <a href="https://redis.io/commands/publish">Redis Documentation: PUBLISH</a>
	 */
	@Nullable
	Long publish(K channel, V message);

	/**
	 * Subscribes the connection to the given channels. Once subscribed, a connection enters listening mode and can only
	 * subscribe to other channels or unsubscribe. No other commands are accepted until the connection is unsubscribed.
	 * <p>
	 * Note that this operation is blocking and the current thread starts waiting for new messages immediately.
	 *
	 * @param listener message listener, must not be {@literal null}.
	 * @param channels channel names, must not be {@literal null}.
	 * @see <a href="https://redis.io/commands/subscribe">Redis Documentation: SUBSCRIBE</a>
	 */
	void subscribe(MessageListener<K, V> listener, K... channels);

	/**
	 * Subscribes the connection to all channels matching the given patterns. Once subscribed, a connection enters
	 * listening mode and can only subscribe to other channels or unsubscribe. No other commands are accepted until the
	 * connection is unsubscribed.
	 * <p>
	 * Note that this operation is blocking and the current thread starts waiting for new messages immediately.
	 *
	 * @param listener message listener, must not be {@literal null}.
	 * @param patterns channel name patterns, must not be {@literal null}.
	 * @see <a href="https://redis.io/commands/psubscribe">Redis Documentation: PSUBSCRIBE</a>
	 */
	void pSubscribe(MessageListener<K, V> listener, K... patterns);
}