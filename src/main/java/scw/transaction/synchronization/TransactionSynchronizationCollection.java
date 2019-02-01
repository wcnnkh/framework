package scw.transaction.synchronization;

import java.util.Iterator;
import java.util.LinkedList;

import scw.transaction.TransactionException;

/**
 * 多个事务组合
 * 
 * @author shuchaowen
 *
 */
public final class TransactionSynchronizationCollection extends LinkedList<TransactionSynchronization>
		implements TransactionSynchronization {
	private static final long serialVersionUID = 1L;

	public void beforeCommit() throws TransactionException {
		Iterator<TransactionSynchronization> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next().beforeCommit();
		}
	}

	public void afterCommit() throws TransactionException {
		Iterator<TransactionSynchronization> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next().afterCommit();
		}
	}

	public void rollback() throws TransactionException {
		Iterator<TransactionSynchronization> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next().rollback();
		}
	}

	public void complete() throws TransactionException {
		Iterator<TransactionSynchronization> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next().complete();
		}
	}
}
