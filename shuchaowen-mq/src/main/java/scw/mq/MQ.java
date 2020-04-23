package scw.mq;

import scw.util.queue.Consumer;

public interface MQ<T> {
	void push(String name, T message);

	void bindConsumer(String name, Consumer<T> consumer);
}
