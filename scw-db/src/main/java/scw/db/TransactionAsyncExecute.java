package scw.db;

import java.util.ArrayList;

import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionManager;
import scw.transaction.TransactionUtils;

public final class TransactionAsyncExecute extends ArrayList<AsyncExecute> implements AsyncExecute {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(TransactionAsyncExecute.class);

	public void execute(DB db) {
		TransactionManager manager = TransactionUtils.getManager();
		Transaction transaction = manager.getTransaction(TransactionDefinition.DEFAULT);
		try {
			for (AsyncExecute execute : this) {
				execute.execute(db);
			}
			manager.commit(transaction);
		} catch (Throwable e) {
			manager.rollback(transaction);
			logger.error(e, this.toString());
		}
	}
}
