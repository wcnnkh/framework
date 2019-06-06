package scw.mq;

import scw.core.BlockingQueue;
import scw.data.redis.Redis;
import scw.data.utils.RedisBlockingQueue;

public final class RedisMQ<T> extends BlockingQueueMQ<T> {
	private final Redis redis;

	public RedisMQ(Redis redis) {
		this.redis = redis;
	}

	@Override
	protected BlockingQueue<T> newQueue(String name) {
		return new RedisBlockingQueue<T>(redis, name);
	}
}
