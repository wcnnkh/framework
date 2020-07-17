package scw.util.queue;

import java.util.ArrayList;
import java.util.List;

import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

public abstract class AbstractQueue<E> implements Consumer<E>, MessageQueue<E> {
	private List<Consumer<E>> consumers = new ArrayList<Consumer<E>>();

	public final void addConsumer(Consumer<E> consumer) {
		consumers.add(consumer);
	}

	public final void consume(E message) throws Throwable {
		Transaction transaction = TransactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			for (Consumer<E> consumer : consumers) {
				consumer.consume(message);
			}
			TransactionManager.commit(transaction);
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
	}
}
