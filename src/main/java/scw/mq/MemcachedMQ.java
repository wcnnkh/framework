package scw.mq;

import scw.core.BlockingQueue;
import scw.data.memcached.Memcached;
import scw.data.utils.MemcachedBlockingQueue;

public final class MemcachedMQ<T> extends BlockingQueueMQ<T> {
	private final Memcached memcached;

	public MemcachedMQ(Memcached memcached) {
		this.memcached = memcached;
	}

	@Override
	protected BlockingQueue<T> newQueue(String name) {
		return new MemcachedBlockingQueue<T>(memcached, name);
	}
}
