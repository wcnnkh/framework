package scw.mq;

import scw.data.redis.Redis;
import scw.data.utils.Queue;
import scw.data.utils.RedisQueue;

public final class RedisMQ<T> extends QueueMQ<T> {
	private final Redis redis;

	public RedisMQ(Redis redis) {
		this.redis = redis;
	}

	@Override
	protected Queue<T> newQueue(String name) {
		return new RedisQueue<T>(redis, name);
	}
}
