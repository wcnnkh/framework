package scw.mq;

import java.util.List;

import scw.core.Consumer;
import scw.core.LinkedMultiValueMap;

public abstract class AbstractMQ<T> implements MQ<T> {
	private LinkedMultiValueMap<String, Consumer<T>> consumerMap = new LinkedMultiValueMap<String, Consumer<T>>();

	public List<Consumer<T>> getConsumerList(String name) {
		return consumerMap.get(name);
	}

	public void bindConsumer(String name, Consumer<T> consumer) {
		synchronized (consumerMap) {
			consumerMap.add(name, consumer);
		}
	}
}
