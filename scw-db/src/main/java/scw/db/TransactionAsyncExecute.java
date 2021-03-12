package scw.db;

import java.util.ArrayList;

import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

public final class TransactionAsyncExecute extends ArrayList<AsyncExecute> implements AsyncExecute {
	private static final long serialVersionUID = 1L;

	public void execute(DB db) {
		Transaction transaction = TransactionManager.GLOBAL.getTransaction(new DefaultTransactionDefinition());
		try {
			for (AsyncExecute execute : this) {
				execute.execute(db);
			}
			TransactionManager.GLOBAL.commit(transaction);
		} catch (Throwable e) {
			TransactionManager.GLOBAL.rollback(transaction);
			e.printStackTrace();
		}
	}
}
