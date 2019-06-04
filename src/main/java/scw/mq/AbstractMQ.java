package scw.mq;

import java.util.HashMap;
import java.util.Map;

import scw.core.Consumer;

public abstract class AbstractMQ<T> implements MQ<T> {
	private Map<String, Consumer<T>> consumerMap = new HashMap<String, Consumer<T>>();

	public Consumer<T> getConsumer(String name) {
		return consumerMap.get(name);
	}

	public void addConsumer(String name, Consumer<T> consumer) {
		synchronized (consumerMap) {
			consumerMap.put(name, consumer);
		}
	}
}
