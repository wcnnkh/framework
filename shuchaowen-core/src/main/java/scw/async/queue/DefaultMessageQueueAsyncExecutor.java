package scw.async.queue;

import scw.async.AsyncRunnable;
import scw.beans.BeanFactory;
import scw.util.queue.BlockingMessageQueue;
import scw.util.queue.LinkedBlockingQueue;

public class DefaultMessageQueueAsyncExecutor extends MessageQueueAsyncExecutor {

	public DefaultMessageQueueAsyncExecutor(BeanFactory beanFactory) {
		super(beanFactory, new BlockingMessageQueue<AsyncRunnable>(new LinkedBlockingQueue<AsyncRunnable>()));
	}

}
