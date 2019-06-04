package scw.mq;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.Consumer;
import scw.core.Init;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.data.utils.Queue;

public class QueueMQ<T> extends AbstractMQ<T> implements Runnable, scw.core.Destroy, Init {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private volatile Map<String, Queue<T>> queueMap = new HashMap<String, Queue<T>>();
	private boolean dynamicCreationQueue;
	private Thread thread = new Thread(this);

	public QueueMQ(boolean dynamicCreationQueue) {
		this.dynamicCreationQueue = dynamicCreationQueue;
	}

	public void addQueue(String name, Queue<T> queue) {
		synchronized (queueMap) {
			queueMap.put(name, queue);
		}
	}

	public void push(String name, T message) {
		Queue<T> queue = queueMap.get(name);
		if (queue == null && !queueMap.containsKey(name) && dynamicCreationQueue) {
			synchronized (queueMap) {
				if (queueMap.containsKey(name)) {
					queue = queueMap.get(name);
				} else {
					queue = newQueue(name);
					queueMap.put(name, queue);
				}
			}
		}

		if (queue == null) {
			logger.warn("推送消弱时找不到指定的队列:{}", name);
		}
		queue.offer(message);
	}

	/**
	 * 创建一个新的队列
	 * 
	 * @param name
	 * @return
	 */
	protected Queue<T> newQueue(String name) {
		return null;
	}

	public void init() {
		thread.start();
	}

	public void destroy() {
		thread.interrupt();
	}

	public void run() {
		try {
			while (!Thread.interrupted()) {
				for (Entry<String, Queue<T>> entry : queueMap.entrySet()) {
					Queue<T> queue = entry.getValue();
					if (queue == null) {
						continue;
					}

					T message = queue.take();
					if (message == null) {
						continue;
					}

					Consumer<T> consumer = getConsumer(entry.getKey());
					if (consumer == null) {
						logger.warn("找不到消费者：{}", entry.getKey());
					}

					consumer.consume(message);
				}
			}
		} catch (InterruptedException e) {
		}
	}
}
