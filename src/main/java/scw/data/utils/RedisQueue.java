package scw.data.utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import scw.data.redis.Redis;

public class RedisQueue<E> implements Queue<E> {
	private final Redis redis;
	private final String queueKey;

	public RedisQueue(Redis redis, String queueKey) {
		this.redis = redis;
		this.queueKey = queueKey;
	}

	public boolean offer(E e) {
		redis.getObjectOperations().lpush(queueKey, e);
		return true;
	}

	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		return offer(e);
	}

	@SuppressWarnings("unchecked")
	public E peek() {
		return (E) redis.getObjectOperations().lindex(queueKey, -1);
	}

	@SuppressWarnings("unchecked")
	public E poll() {
		return (E) redis.getObjectOperations().rpop(queueKey);
	}

	public void put(E e) throws InterruptedException {
		offer(e);
	}

	@SuppressWarnings("unchecked")
	public E take() throws InterruptedException {
		List<Object> dataList = redis.getObjectOperations().brpop(Integer.MAX_VALUE, queueKey);
		if (dataList == null || dataList.size() != 2) {
			return null;
		}

		Object v1 = dataList.get(0);
		if (v1 == null) {
			return null;
		}

		return (E) dataList.get(1);
	}

}
