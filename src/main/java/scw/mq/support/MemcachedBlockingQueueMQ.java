package scw.mq.support;

import scw.data.memcached.Memcached;
import scw.mq.BlockingQueueMQ;
import scw.mq.SingleBlockingQueueMQ;

public class MemcachedBlockingQueueMQ<T> extends BlockingQueueMQ<T> {
	private final Memcached memcached;
	private final boolean transaction;// 对同一个name添加消费者是多消费者是否使用事务

	public MemcachedBlockingQueueMQ(Memcached memcached, boolean transaction) {
		this.memcached = memcached;
		this.transaction = transaction;
	}

	@Override
	protected SingleBlockingQueueMQ<T> createSingleBlockingQueueMQ(String name) {
		return new MemcachedSingleBlockingQueueMQ<T>(memcached, name,
				transaction);
	}

}
