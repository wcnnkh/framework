package scw.mq;

import scw.core.Consumer;

public interface MQ<T> {
	void push(String name, T message);

	void bindConsumer(String name, Consumer<T> consumer);
}
