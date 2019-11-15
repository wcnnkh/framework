package scw.db;

import java.util.LinkedList;

import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

public final class TransactionAsyncExecute extends LinkedList<AsyncExecute> implements AsyncExecute {
	private static final long serialVersionUID = 1L;

	public void execute(DB db) {
		Transaction transaction = TransactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			for (AsyncExecute execute : this) {
				execute.execute(db);
			}
			TransactionManager.commit(transaction);
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
		}
	}
}
