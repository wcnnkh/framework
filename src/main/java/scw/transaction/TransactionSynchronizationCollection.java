package scw.transaction;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * 多个事务组合
 * @author shuchaowen
 *
 */
public final class TransactionSynchronizationCollection extends LinkedList<TransactionSynchronization>
		implements TransactionSynchronization {
	private static final long serialVersionUID = 1L;

	public void beforeCommit() throws Throwable {
		Iterator<TransactionSynchronization> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next().beforeCommit();
		}
	}

	public void commit() throws Throwable {
		Iterator<TransactionSynchronization> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next().commit();
		}
	}

	public void afterCommit() throws Throwable {
		Iterator<TransactionSynchronization> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next().afterCommit();
		}
	}

	public void rollback() {
		Iterator<TransactionSynchronization> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next().rollback();
		}
	}

	public void complete() {
		Iterator<TransactionSynchronization> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next().complete();
		}
	}
}
