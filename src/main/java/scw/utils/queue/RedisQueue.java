package scw.utils.queue;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import scw.common.utils.IOUtils;
import scw.redis.Redis;

public class RedisQueue<E> implements Queue<E> {
	private final Redis redis;
	private final Charset charset;
	private final String queueKey;

	public RedisQueue(Redis redis, Charset charset, String queueKey) {
		this.redis = redis;
		this.charset = charset;
		this.queueKey = queueKey;
	}

	public boolean offer(E e) {
		byte[] data = IOUtils.javaObjectToByte(e);
		redis.lpush(queueKey.getBytes(charset), data);
		return true;
	}

	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		return offer(e);
	}

	public E peek() {
		byte[] data = redis.lindex(queueKey.getBytes(charset), -1);
		if (data == null) {
			return null;
		}
		return IOUtils.byteToJavaObject(data);
	}

	public E poll() {
		byte[] data = redis.rpop(queueKey.getBytes(charset));
		if (data == null) {
			return null;
		}
		return IOUtils.byteToJavaObject(data);
	}

	public void put(E e) throws InterruptedException {
		offer(e);
	}

	public E take() throws InterruptedException {
		List<byte[]> dataList = redis.brpop(queueKey.getBytes(charset));
		if (dataList == null || dataList.isEmpty()) {
			return null;
		}

		byte[] data = dataList.get(0);
		if (data == null) {
			return null;
		}
		return IOUtils.byteToJavaObject(data);
	}

}
