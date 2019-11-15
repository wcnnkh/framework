package scw.mq.queue;

import scw.core.Destroy;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public final class DefaultQueue<E> extends AbstractQueue<E> implements Runnable, Destroy {
	private static Logger logger = LoggerUtils.getLogger(DefaultQueue.class);
	private BlockingQueue<E> blockingQueue;
	private Thread thread;

	public DefaultQueue() {
		this(new DefaultBlockingQueue<E>());
	}

	public DefaultQueue(BlockingQueue<E> blockingQueue) {
		this.blockingQueue = blockingQueue;
		this.thread = new Thread(this, getClass().getName());
		thread.start();
	}

	public DefaultQueue(Memcached memcached, String queueKey) {
		this(new MemcachedBlockingQueue<E>(memcached, queueKey));
	}

	public DefaultQueue(Redis redis, String queueKey) {
		this(new RedisBlockingQueue<E>(redis, queueKey));
	}

	public void push(E message) {
		try {
			blockingQueue.put(message);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void destroy() {
		if (thread != null) {
			thread.interrupt();
		}
	}

	public void run() {
		try {
			while (!thread.isInterrupted()) {
				E message = blockingQueue.take();
				try {
					consume(message);
				} catch (Throwable e) {
					logger.error(e, "消费者异常");
				}
			}
		} catch (InterruptedException e) {
		}
	}
}
