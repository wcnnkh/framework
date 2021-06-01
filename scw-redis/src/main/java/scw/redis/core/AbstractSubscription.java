/*
 * Copyright 2011-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scw.redis.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.core.Assert;
import scw.core.utils.ObjectUtils;
import scw.lang.Nullable;

/**
 * Base implementation for a subscription handling the channel/pattern
 * registration so subclasses only have to deal with the actual
 * registration/unregistration.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSubscription<K, V> implements Subscription<K, V> {

	private final Collection<K> channels = new ArrayList<>(2);
	private final Collection<K> patterns = new ArrayList<>(2);
	private final AtomicBoolean alive = new AtomicBoolean(true);
	private final MessageListener<K, V> listener;

	protected AbstractSubscription(MessageListener<K, V> listener) {
		this(listener, null, null);
	}

	/**
	 * Constructs a new <code>AbstractSubscription</code> instance. Allows
	 * channels and patterns to be added to the subscription w/o triggering a
	 * subscription action (as some clients (Jedis) require an initial call
	 * before entering into listening mode).
	 *
	 * @param listener
	 *            must not be {@literal null}.
	 * @param channels
	 *            can be {@literal null}.
	 * @param patterns
	 *            can be {@literal null}.
	 */
	protected AbstractSubscription(MessageListener<K, V> listener,
			@Nullable K[] channels, @Nullable K[] patterns) {

		Assert.notNull(listener, "MessageListener must not be null!");

		this.listener = listener;

		synchronized (this.channels) {
			add(this.channels, channels);
		}
		synchronized (this.patterns) {
			add(this.patterns, patterns);
		}
	}

	/**
	 * Subscribe to the given channels.
	 *
	 * @param channels
	 *            channels to subscribe to
	 */
	protected abstract void doSubscribe(K... channels);

	/**
	 * Channel unsubscribe.
	 *
	 * @param all
	 *            true if all the channels are unsubscribed (used as a hint for
	 *            the underlying implementation).
	 * @param channels
	 *            channels to be unsubscribed
	 */
	protected abstract void doUnsubscribe(boolean all, K... channels);

	/**
	 * Subscribe to the given patterns
	 *
	 * @param patterns
	 *            patterns to subscribe to
	 */
	protected abstract void doPsubscribe(K... patterns);

	/**
	 * Pattern unsubscribe.
	 *
	 * @param all
	 *            true if all the patterns are unsubscribed (used as a hint for
	 *            the underlying implementation).
	 * @param patterns
	 *            patterns to be unsubscribed
	 */
	protected abstract void doPUnsubscribe(boolean all, K... patterns);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.connection.Subscription#close()
	 */
	@Override
	public void close() {
		doClose();
	}

	/**
	 * Shutdown the subscription and free any resources held.
	 */
	protected abstract void doClose();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.connection.Subscription#getListener()
	 */
	@Override
	public MessageListener<K, V> getListener() {
		return listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.connection.Subscription#getChannels()
	 */
	@Override
	public Collection<K> getChannels() {
		synchronized (channels) {
			return clone(channels);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.connection.Subscription#getPatterns()
	 */
	@Override
	public Collection<K> getPatterns() {
		synchronized (patterns) {
			return clone(patterns);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.redis.connection.Subscription#pSubscribe(byte
	 * [][])
	 */
	@Override
	public void pSubscribe(K... patterns) {
		checkPulse();

		Assert.notEmpty(patterns, "at least one pattern required");

		synchronized (this.patterns) {
			add(this.patterns, patterns);
		}

		doPsubscribe(patterns);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.redis.connection.Subscription#pUnsubscribe()
	 */
	@Override
	public void pUnsubscribe() {
		pUnsubscribe((K[]) null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.redis.connection.Subscription#subscribe(byte
	 * [][])
	 */
	@Override
	public void subscribe(K... channels) {
		checkPulse();

		Assert.notEmpty(channels, "at least one channel required");

		synchronized (this.channels) {
			add(this.channels, channels);
		}

		doSubscribe(channels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.connection.Subscription#unsubscribe()
	 */
	@Override
	public void unsubscribe() {
		unsubscribe((K[]) null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.redis.connection.Subscription#pUnsubscribe(byte
	 * [][])
	 */

	@Override
	public void pUnsubscribe(@Nullable K... patts) {
		if (!isAlive()) {
			return;
		}

		// shortcut for unsubscribing all patterns
		if (ObjectUtils.isEmpty(patts)) {
			if (!this.patterns.isEmpty()) {
				synchronized (this.patterns) {
					patts = (K[]) getPatterns().toArray();
					doPUnsubscribe(true, patts);
					this.patterns.clear();
				}
			} else {
				// nothing to unsubscribe from
				return;
			}
		} else {
			doPUnsubscribe(false, patts);
			synchronized (this.patterns) {
				remove(this.patterns, patts);
			}
		}

		closeIfUnsubscribed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.redis.connection.Subscription#unsubscribe(byte
	 * [][])
	 */
	@Override
	public void unsubscribe(@Nullable K... chans) {
		if (!isAlive()) {
			return;
		}

		// shortcut for unsubscribing all channels
		if (ObjectUtils.isEmpty(chans)) {
			if (!this.channels.isEmpty()) {
				synchronized (this.channels) {
					chans = (K[]) getChannels().toArray();
					doUnsubscribe(true, chans);
					this.channels.clear();
				}
			} else {
				// nothing to unsubscribe from
				return;
			}
		} else {
			doUnsubscribe(false, chans);
			synchronized (this.channels) {
				remove(this.channels, chans);
			}
		}

		closeIfUnsubscribed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.connection.Subscription#isAlive()
	 */
	@Override
	public boolean isAlive() {
		return alive.get();
	}

	private void checkPulse() {
		if (!isAlive()) {
			throw new RedisInvalidSubscriptionException(
					"Subscription has been unsubscribed and cannot be used anymore");
		}
	}

	private void closeIfUnsubscribed() {
		if (channels.isEmpty() && patterns.isEmpty()) {
			alive.set(false);
			doClose();
		}
	}

	private static <K> Collection<K> clone(Collection<K> col) {
		Collection<K> list = new ArrayList<>(col.size());
		for (K wrapper : col) {
			list.add(wrapper);
		}
		return list;
	}

	private static <K> void add(Collection<K> col, @Nullable K... bytes) {
		if (!ObjectUtils.isEmpty(bytes)) {
			for (K bs : bytes) {
				col.add(bs);
			}
		}
	}

	private static <K> void remove(Collection<K> col, @Nullable K... bytes) {
		if (!ObjectUtils.isEmpty(bytes)) {
			for (K bs : bytes) {
				col.remove(bs);
			}
		}
	}
}
