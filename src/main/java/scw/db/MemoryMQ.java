package scw.db;

import scw.data.utils.MemoryQueue;
import scw.data.utils.Queue;
import scw.mq.QueueMQ;

public final class MemoryMQ<T> extends QueueMQ<T> {

	public MemoryMQ(boolean dynamicCreationQueue) {
		super(dynamicCreationQueue);
	}

	@Override
	protected Queue<T> newQueue(String name) {
		return new MemoryQueue<T>();
	}
}
