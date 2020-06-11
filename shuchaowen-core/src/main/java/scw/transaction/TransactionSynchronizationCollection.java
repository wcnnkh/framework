package scw.transaction;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

final class TransactionSynchronizationCollection extends LinkedList<TransactionSynchronization>
		implements TransactionSynchronization {
	private static final long serialVersionUID = 1L;

	public void commit() throws Throwable {
		Iterator<TransactionSynchronization> iterator = iterator();
		while (iterator.hasNext()) {
			TransactionSynchronization transaction = iterator.next();
			if (transaction != null) {
				transaction.commit();
			}
		}
	}

	public void rollback() {
		ListIterator<TransactionSynchronization> iterator = listIterator(size());
		while (iterator.hasPrevious()) {
			TransactionSynchronization transaction = iterator.previous();
			if (transaction != null) {
				try {
					transaction.rollback();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void completion() {
		ListIterator<TransactionSynchronization> iterator = listIterator(size());
		while (iterator.hasPrevious()) {
			TransactionSynchronization transaction = iterator.previous();
			if (transaction != null) {
				try {
					transaction.completion();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}
}
