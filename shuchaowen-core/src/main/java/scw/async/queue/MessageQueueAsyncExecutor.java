package scw.async.queue;

import scw.async.AbstractAsyncExecutor;
import scw.async.AsyncRunnable;
import scw.beans.BeanFactory;
import scw.util.queue.Consumer;
import scw.util.queue.MessageQueue;

public class MessageQueueAsyncExecutor extends AbstractAsyncExecutor implements Consumer<AsyncRunnable> {
	private MessageQueue<AsyncRunnable> messageQueue;

	public MessageQueueAsyncExecutor(BeanFactory beanFactory, MessageQueue<AsyncRunnable> messageQueue) {
		super(beanFactory);
		this.messageQueue = messageQueue;
		messageQueue.addConsumer(this);
	}

	public MessageQueue<AsyncRunnable> getMessageQueue() {
		return messageQueue;
	}

	public void execute(AsyncRunnable asyncRunnable) {
		messageQueue.push(asyncRunnable);
	}

	public void consume(AsyncRunnable message) throws Throwable {
		call(message);
	}

}
