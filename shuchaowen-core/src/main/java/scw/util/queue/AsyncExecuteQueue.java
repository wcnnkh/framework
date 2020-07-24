package scw.util.queue;

import scw.util.Consumer;

public interface AsyncExecuteQueue<E> {
	void put(E message);

	void addConsumer(Consumer<E> consumer);
}
