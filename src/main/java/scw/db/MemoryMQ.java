package scw.db;

import scw.core.BlockingQueue;
import scw.data.utils.MemoryQueue;
import scw.mq.BlockingQueueMQ;

public final class MemoryMQ<T> extends BlockingQueueMQ<T> {

	@Override
	protected BlockingQueue<T> newQueue(String name) {
		return new MemoryQueue<T>();
	}
}
