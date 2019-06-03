package scw.mq;

import java.util.Iterator;
import java.util.LinkedList;

import scw.core.Consumer;

public abstract class AbstractMQ<T> implements MQ<T> {
	private LinkedList<Consumer<T>> consumers = new LinkedList<Consumer<T>>();

	public synchronized void addConsumer(Consumer<T> consumer) {
		consumers.add(consumer);
	};

	protected void execute(T message) throws Throwable {
		Iterator<Consumer<T>> iterator = consumers.iterator();
		while (iterator.hasNext()) {
			iterator.next().consume(message);
		}
	}
}
