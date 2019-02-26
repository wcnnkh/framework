package scw.transaction;

import java.util.Iterator;
import java.util.LinkedList;

final class TransactionSynchronizationCollection extends LinkedList<TransactionSynchronization>
		implements TransactionSynchronization {
	private static final long serialVersionUID = 1L;
	private int beginTag = 0;
	private int processTag = 0;

	public void process() {
		Iterator<TransactionSynchronization> iterator = iterator();
		for (; iterator.hasNext(); processTag++) {
			TransactionSynchronization transaction = iterator.next();
			if (transaction != null) {
				transaction.process();
			}
		}
	}

	public void end() {
		Iterator<TransactionSynchronization> iterator = iterator();
		for (; beginTag >= 0 && iterator.hasNext(); beginTag--) {
			TransactionSynchronization transaction = iterator.next();
			if (transaction != null) {
				transaction.end();
			}
		}
	}

	public void rollback() {
		Iterator<TransactionSynchronization> iterator = iterator();
		for (; processTag >= 0 && iterator.hasNext(); processTag--) {
			TransactionSynchronization transaction = iterator.next();
			if (transaction != null) {
				transaction.rollback();
			}
		}
	}
}
