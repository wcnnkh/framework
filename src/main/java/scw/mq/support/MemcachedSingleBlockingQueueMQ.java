package scw.mq.support;

import scw.data.memcached.Memcached;
import scw.data.utils.MemcachedBlockingQueue;
import scw.mq.SingleBlockingQueueMQ;

public class MemcachedSingleBlockingQueueMQ<T> extends SingleBlockingQueueMQ<T> {
	public MemcachedSingleBlockingQueueMQ(Memcached memcached,
			String queueName, boolean transaction) {
		super(new MemcachedBlockingQueue<T>(memcached, queueName), transaction);
	}

}
