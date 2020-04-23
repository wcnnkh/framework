package scw.data.queue;

import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.util.queue.BlockingMessageQueue;

public class DataMessageQueue<E> extends BlockingMessageQueue<E> {

	public DataMessageQueue(Redis redis, String queueKey) {
		super(new RedisBlockingQueue<E>(redis, queueKey));
	}

	public DataMessageQueue(Memcached memcached, String queueKey) {
		super(new MemcachedBlockingQueue<E>(memcached, queueKey));
	}
}
