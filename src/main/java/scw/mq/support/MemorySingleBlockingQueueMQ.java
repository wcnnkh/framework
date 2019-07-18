package scw.mq.support;

import scw.data.utils.DefaultBlockingQueue;
import scw.mq.SingleBlockingQueueMQ;

public class MemorySingleBlockingQueueMQ<T> extends SingleBlockingQueueMQ<T> {

	public MemorySingleBlockingQueueMQ(boolean transaction) {
		super(new DefaultBlockingQueue<T>(), transaction);
	}
}
