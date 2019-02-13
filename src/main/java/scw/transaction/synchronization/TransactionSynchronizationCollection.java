package scw.transaction.synchronization;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import scw.transaction.TransactionException;

public final class TransactionSynchronizationCollection extends LinkedList<TransactionSynchronization>
		implements TransactionSynchronization {
	private static final long serialVersionUID = 1L;
	private int beginTag = 0;
	private int processTag = 0;

	public TransactionSynchronizationCollection() {
		super();
	}

	public TransactionSynchronizationCollection(Collection<? extends TransactionSynchronization> transactions) {
		super(transactions);
	}

	public void begin() throws TransactionException {
		Iterator<TransactionSynchronization> iterator = iterator();
		for (; iterator.hasNext(); beginTag++) {
			TransactionSynchronization transaction = iterator.next();
			if (transaction != null) {
				transaction.begin();
			}
		}
	}

	public void commit() throws TransactionException {
		Iterator<TransactionSynchronization> iterator = iterator();
		for (; iterator.hasNext(); processTag++) {
			TransactionSynchronization transaction = iterator.next();
			if (transaction != null) {
				transaction.commit();
			}
		}
	}

	public void end() throws TransactionException {
		Iterator<TransactionSynchronization> iterator = iterator();
		for (; beginTag >= 0 && iterator.hasNext(); beginTag--) {
			TransactionSynchronization transaction = iterator.next();
			if (transaction != null) {
				transaction.end();
			}
		}
	}

	public void rollback() throws TransactionException {
		Iterator<TransactionSynchronization> iterator = iterator();
		for (; processTag >= 0 && iterator.hasNext(); processTag--) {
			TransactionSynchronization transaction = iterator.next();
			if (transaction != null) {
				try {
					transaction.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
