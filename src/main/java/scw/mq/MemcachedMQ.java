package scw.mq;

import scw.data.memcached.Memcached;
import scw.data.utils.MemcachedQueue;
import scw.data.utils.Queue;

public final class MemcachedMQ<T> extends QueueMQ<T> {
	private final Memcached memcached;

	public MemcachedMQ(boolean dynamicCreationQueue, Memcached memcached) {
		super(dynamicCreationQueue);
		this.memcached = memcached;
	}

	@Override
	protected Queue<T> newQueue(String name) {
		return new MemcachedQueue<T>(memcached, name);
	}
}
