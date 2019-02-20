package scw.transaction.sql;

import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.TransactionManager;

public final class SqlTransactionManager implements TransactionManager {

	public Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
		return SqlTransactionUtils.getTransaction(transactionDefinition);
	}

	public void commit(Transaction transaction) throws TransactionException {
		SqlTransactionUtils.commit((MultipleConnectionTransactionSynchronization) transaction);
	}

	public void rollback(Transaction transaction) throws TransactionException {
		SqlTransactionUtils.rollback((MultipleConnectionTransactionSynchronization) transaction);
	}

	public boolean hasTransaction() {
		return SqlTransactionUtils.hasTransaction();
	}

}
