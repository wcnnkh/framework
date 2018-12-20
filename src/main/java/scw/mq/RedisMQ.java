package scw.mq;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.io.IOUtils;
import scw.common.utils.XUtils;
import scw.locks.RedisLock;
import scw.redis.Redis;

public final class RedisMQ<T> implements MQ<T> {
	private static final String QUEUE_READ_LOCK = "_lock";

	private final Redis redis;
	private final String queueKey;
	private final Charset charset;
	private final Thread thread;
	private List<Consumer<T>> consumerList;
	
	public RedisMQ(final Redis redis, final String queueKey, String charsetName){
		this(redis, queueKey, Charset.forName(charsetName));
	}

	public RedisMQ(final Redis redis, final String queueKey, final Charset charset) {
		this.charset = charset;
		this.queueKey = queueKey;
		this.redis = redis;
		this.thread = new Thread(new Runnable() {

			public void run() {
				long sleepTime = 0L;
				try {
					while (!Thread.interrupted()) {
						if (consumerList == null || consumerList.isEmpty()) {
							continue;
						}

						RedisLock redisLock = new RedisLock(redis, queueKey + QUEUE_READ_LOCK, XUtils.getUUID(), 600 * consumerList.size());
						if (redisLock.lock()) {
							try {
								byte[] data = redis.lindex(queueKey.getBytes(charset), -1);// 找到尾部元素
								if (data != null) {
									T t = IOUtils.byteToJavaObject(data);
									if (t != null) {
										for (Consumer<T> consumer : consumerList) {
											consumer.handler(t);
										}
										redis.rpop(queueKey.getBytes(charset));
										sleepTime = 0;
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								redisLock.unlock();
							}
						}

						if (sleepTime > 0) {
							Thread.sleep(sleepTime);
						}
						sleepTime = Math.min(5000L, sleepTime + 100);
					}
				} catch (InterruptedException e) {
				}
			}
		}, queueKey);
	}

	public void push(T message) {
		byte[] data;
		try {
			data = IOUtils.javaObjectToByte(message);
			redis.lpush(queueKey.getBytes(charset), data);
		} catch (IOException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public synchronized void consumer(Consumer<T> consumer) {
		if (consumerList == null) {
			consumerList = new ArrayList<Consumer<T>>();
		}
		consumerList.add(consumer);
	}

	public void destroy() {
		thread.interrupt();
	}

	public void start() {
		thread.start();
	}
}
