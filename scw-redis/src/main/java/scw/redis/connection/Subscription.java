package scw.redis.connection;

import java.util.Collection;

/**
 * Subscription for Redis channels. Just like the underlying {@link RedisConnection}, it should not be used by multiple
 * threads. Note that once a subscription died, it cannot accept any more subscriptions.
 *
 */
public interface Subscription {

	/**
	 * Adds the given channels to the current subscription.
	 *
	 * @param channels channel names. Must not be empty.
	 */
	void subscribe(byte[]... channels) throws RedisInvalidSubscriptionException;

	/**
	 * Adds the given channel patterns to the current subscription.
	 *
	 * @param patterns channel patterns. Must not be empty.
	 */
	void pSubscribe(byte[]... patterns) throws RedisInvalidSubscriptionException;

	/**
	 * Cancels the current subscription for all channels given by name.
	 */
	void unsubscribe();

	/**
	 * Cancels the current subscription for all given channels.
	 *
	 * @param channels channel names. Must not be empty.
	 */
	void unsubscribe(byte[]... channels);

	/**
	 * Cancels the subscription for all channels matched by patterns.
	 */
	void pUnsubscribe();

	/**
	 * Cancels the subscription for all channels matching the given patterns.
	 *
	 * @param patterns must not be empty.
	 */
	void pUnsubscribe(byte[]... patterns);

	/**
	 * Returns the (named) channels for this subscription.
	 *
	 * @return collection of named channels
	 */
	Collection<byte[]> getChannels();

	/**
	 * Returns the channel patters for this subscription.
	 *
	 * @return collection of channel patterns
	 */
	Collection<byte[]> getPatterns();

	/**
	 * Returns the listener used for this subscription.
	 *
	 * @return the listener used for this subscription.
	 */
	MessageListener getListener();

	/**
	 * Indicates whether this subscription is still 'alive' or not.
	 *
	 * @return true if the subscription still applies, false otherwise.
	 */
	boolean isAlive();

	/**
	 * Shutdown the subscription and free any resources held.
	 *
	 */
	void close();
}