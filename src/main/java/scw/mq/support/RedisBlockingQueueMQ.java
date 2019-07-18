package scw.mq.support;

import scw.data.redis.Redis;
import scw.mq.BlockingQueueMQ;
import scw.mq.SingleBlockingQueueMQ;

public class RedisBlockingQueueMQ<T> extends BlockingQueueMQ<T> {
	private final Redis redis;;
	private final boolean transaction;

	public RedisBlockingQueueMQ(Redis redis, boolean transaction) {
		this.redis = redis;
		this.transaction = transaction;
	}

	@Override
	protected SingleBlockingQueueMQ<T> createSingleBlockingQueueMQ(String name) {
		return new RedisSingleBlockingQueueMQ<T>(redis, name, transaction);
	}

}
