package scw.mq;

import java.util.Map.Entry;

import scw.core.Consumer;
import scw.core.Destroy;
import scw.core.lazy.MapLazyFactory;
import scw.core.lazy.SafeMapLazyFactory;

public abstract class BlockingQueueMQ<T> implements MQ<T>, Destroy {
	private MapLazyFactory<String, SingleBlockingQueueMQ<T>> map = new SafeMapLazyFactory<String, SingleBlockingQueueMQ<T>>() {

		public SingleBlockingQueueMQ<T> createValue(String key) {
			SingleBlockingQueueMQ<T> mq = createSingleBlockingQueueMQ(key);
			mq.init();
			return mq;
		}
	};

	protected abstract SingleBlockingQueueMQ<T> createSingleBlockingQueueMQ(
			String name);

	public void push(String name, T message) {
		try {
			map.get(name).push(message);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void bindConsumer(String name, Consumer<T> consumer) {
		map.get(name).addConsumer(consumer);
	}

	public void destroy() {
		for (Entry<String, SingleBlockingQueueMQ<T>> entry : map.entrySet()) {
			entry.getValue().destroy();
		}
	}
}
