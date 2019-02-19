package scw.transaction.def;

import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.TransactionManager;

public class DefaultTransactionManager implements TransactionManager {

	public Transaction getTransaction(
			TransactionDefinition transactionDefinition)
			throws TransactionException {
		return DefaultTransactionUtils.getTransaction(transactionDefinition);
	}

	public void commit(Transaction transaction) throws TransactionException {
		DefaultTransactionUtils
				.commit((MultipleConnectionTransactionSynchronization) transaction);
	}

	public void rollback(Transaction transaction) throws TransactionException {
		DefaultTransactionUtils
				.rollback((MultipleConnectionTransactionSynchronization) transaction);
	}

}
