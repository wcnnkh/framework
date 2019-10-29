package scw.mq;

import java.util.Iterator;
import java.util.LinkedList;

import scw.core.Consumer;
import scw.core.Destroy;
import scw.core.Init;
import scw.data.utils.BlockingQueue;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

/**
 * 使用阻塞队列实现MQ
 * 
 * @author asus1
 *
 * @param <T>
 */
public class SingleBlockingQueueMQ<T> implements Init, Destroy, Runnable {
	private static Logger logger = LoggerUtils.getLogger(SingleBlockingQueueMQ.class);
	private final BlockingQueue<T> blockingQueue;
	private final Thread thread = new Thread(this);
	private final boolean transaction;
	private final LinkedList<Consumer<T>> consumers = new LinkedList<Consumer<T>>();

	public void push(T messsage) throws InterruptedException {
		blockingQueue.put(messsage);
	}

	public void addConsumer(Consumer<T> consumer) {
		consumers.add(consumer);
	}

	/**
	 * @param blockingQueue
	 * @param transaction
	 */
	public SingleBlockingQueueMQ(BlockingQueue<T> blockingQueue, boolean transaction) {
		this.blockingQueue = blockingQueue;
		this.transaction = transaction;
	}

	public void destroy() {
		thread.interrupt();
	}

	public void init() {
		thread.start();
	}

	public void run() {
		try {
			while (!thread.isInterrupted()) {
				T message = blockingQueue.take();
				if (transaction) {
					Transaction transaction = TransactionManager.getTransaction(new DefaultTransactionDefinition());
					try {
						Iterator<Consumer<T>> iterator = consumers.iterator();
						while (iterator.hasNext()) {
							iterator.next().consume(message);
						}
						TransactionManager.commit(transaction);
					} catch (Throwable e) {
						logger.error(e, "消费者异常");
						TransactionManager.rollback(transaction);
					}
				} else {
					Iterator<Consumer<T>> iterator = consumers.iterator();
					while (iterator.hasNext()) {
						try {
							iterator.next().consume(message);
						} catch (Throwable e) {
							logger.error(e, "消费者异常");
						}
					}
				}
			}
		} catch (InterruptedException e) {
		}
	}

}
