package scw.mq.support;

import scw.mq.BlockingQueueMQ;
import scw.mq.SingleBlockingQueueMQ;

public class MemoryBlockingQueueMQ<T> extends BlockingQueueMQ<T> {
	private final boolean transaction;

	public MemoryBlockingQueueMQ(boolean transaction) {
		this.transaction = transaction;
	}

	@Override
	protected SingleBlockingQueueMQ<T> createSingleBlockingQueueMQ(String name) {
		return new MemorySingleBlockingQueueMQ<T>(transaction);
	}

}
