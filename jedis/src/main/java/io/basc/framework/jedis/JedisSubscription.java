package io.basc.framework.jedis;

import io.basc.framework.lang.Nullable;
import io.basc.framework.redis.AbstractSubscription;
import io.basc.framework.redis.MessageListener;
import redis.clients.jedis.BinaryJedisPubSub;

/**
 * Jedis specific subscription.
 *
 * @author Costin Leau
 */
class JedisSubscription extends AbstractSubscription<byte[], byte[]> {

	private final BinaryJedisPubSub jedisPubSub;

	JedisSubscription(MessageListener<byte[], byte[]> listener, BinaryJedisPubSub jedisPubSub,
			@Nullable byte[][] channels, @Nullable byte[][] patterns) {
		super(listener, channels, patterns);
		this.jedisPubSub = jedisPubSub;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	protected void doClose() {
		if (!getChannels().isEmpty()) {
			jedisPubSub.unsubscribe();
		}
		if (!getPatterns().isEmpty()) {
			jedisPubSub.punsubscribe();
		}
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	protected void doPsubscribe(byte[]... patterns) {
		jedisPubSub.psubscribe(patterns);
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	protected void doPUnsubscribe(boolean all, byte[]... patterns) {
		if (all) {
			jedisPubSub.punsubscribe();
		} else {
			jedisPubSub.punsubscribe(patterns);
		}
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	protected void doSubscribe(byte[]... channels) {
		jedisPubSub.subscribe(channels);
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	protected void doUnsubscribe(boolean all, byte[]... channels) {
		if (all) {
			jedisPubSub.unsubscribe();
		} else {
			jedisPubSub.unsubscribe(channels);
		}
	}
}
