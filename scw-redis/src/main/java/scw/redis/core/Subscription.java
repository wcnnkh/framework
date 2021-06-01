package scw.redis.core;

import java.util.Collection;

/**
 * Subscription for Redis channels. Just like the underlying {@link RedisConnection}, it should not be used by multiple
 * threads. Note that once a subscription died, it cannot accept any more subscriptions.
 *
 */
@SuppressWarnings("unchecked")
public interface Subscription<K, V> {

	/**
	 * Adds the given channels to the current subscription.
	 *
	 * @param channels channel names. Must not be empty.
	 */
	void subscribe(K... channels) throws RedisInvalidSubscriptionException;

	/**
	 * Adds the given channel patterns to the current subscription.
	 *
	 * @param patterns channel patterns. Must not be empty.
	 */
	void pSubscribe(K... patterns) throws RedisInvalidSubscriptionException;

	/**
	 * Cancels the current subscription for all channels given by name.
	 */
	void unsubscribe();

	/**
	 * Cancels the current subscription for all given channels.
	 *
	 * @param channels channel names. Must not be empty.
	 */
	void unsubscribe(K... channels);

	/**
	 * Cancels the subscription for all channels matched by patterns.
	 */
	void pUnsubscribe();

	/**
	 * Cancels the subscription for all channels matching the given patterns.
	 *
	 * @param patterns must not be empty.
	 */
	void pUnsubscribe(K... patterns);

	/**
	 * Returns the (named) channels for this subscription.
	 *
	 * @return collection of named channels
	 */
	Collection<K> getChannels();

	/**
	 * Returns the channel patters for this subscription.
	 *
	 * @return collection of channel patterns
	 */
	Collection<K> getPatterns();

	/**
	 * Returns the listener used for this subscription.
	 *
	 * @return the listener used for this subscription.
	 */
	MessageListener<K, V> getListener();

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