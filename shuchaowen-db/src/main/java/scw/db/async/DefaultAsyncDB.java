package scw.db.async;

import scw.async.AsyncRunnable;
import scw.async.queue.MessageQueueAsyncExecutor;
import scw.beans.BeanFactory;
import scw.core.Destroy;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.XUtils;
import scw.db.DB;
import scw.util.queue.BlockingMessageQueue;
import scw.util.queue.BlockingQueue;
import scw.util.queue.LinkedBlockingQueue;

@Configuration(order=Integer.MIN_VALUE)
public class DefaultAsyncDB extends AsyncDB implements Destroy {
	public DefaultAsyncDB(DB db, BeanFactory beanFactory) {
		this(db, beanFactory, new LinkedBlockingQueue<AsyncRunnable>());
	}

	public DefaultAsyncDB(DB db, BeanFactory beanFactory, BlockingQueue<AsyncRunnable> blockingQueue) {
		super(db, new MessageQueueAsyncExecutor(beanFactory, new BlockingMessageQueue<AsyncRunnable>(blockingQueue)));
	}

	public void destroy() throws Exception {
		MessageQueueAsyncExecutor messageQueueAsyncExecutor = (MessageQueueAsyncExecutor) getAsyncExecutor();
		XUtils.destroy(messageQueueAsyncExecutor.getMessageQueue());
	}
}
