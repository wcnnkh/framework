package scw.mq.support;

import scw.data.redis.Redis;
import scw.data.utils.RedisBlockingQueue;
import scw.mq.SingleBlockingQueueMQ;

public class RedisSingleBlockingQueueMQ<T> extends SingleBlockingQueueMQ<T> {

	public RedisSingleBlockingQueueMQ(Redis redis, String queueName,
			boolean transaction) {
		super(new RedisBlockingQueue<T>(redis, queueName), transaction);
	}

}
