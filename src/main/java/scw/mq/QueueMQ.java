package scw.mq;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.Consumer;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.data.utils.Queue;

public class QueueMQ<T> extends AbstractMQ<T> implements scw.core.Destroy {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private volatile Map<String, MyQueueMQ> queueMap = new HashMap<String, MyQueueMQ>();

	public void push(String name, T message) {
		MyQueueMQ queue = queueMap.get(name);
		if (queue == null && !queueMap.containsKey(name)) {
			synchronized (queueMap) {
				if (queueMap.containsKey(name)) {
					queue = queueMap.get(name);
				} else {
					Queue<T> q = newQueue(name);
					if (q != null) {
						queue = new MyQueueMQ(name, q);
						queue.init();
						queueMap.put(name, queue);
					}
				}
			}
		}

		if (queue == null) {
			logger.warn("推送消弱时找不到指定的队列:{}", name);
		}
		queue.push(message);
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

	public void destroy() {
		for (Entry<String, MyQueueMQ> entry : queueMap.entrySet()) {
			entry.getValue().destroy();
		}
	}

	final class MyQueueMQ extends SingleQueueMQ<T> {
		private final String name;

		public MyQueueMQ(String name, Queue<T> queue) {
			super(queue);
			this.name = name;
		}

		@Override
		public void consume(T message) {
			Consumer<T> consumer = getConsumer(name);
			if (consumer == null) {
				return;
			}

			consumer.consume(message);
		}
	}
}
