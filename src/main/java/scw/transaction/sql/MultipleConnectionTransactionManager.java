package scw.transaction.sql;

import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.TransactionManager;

public class MultipleConnectionTransactionManager implements TransactionManager {

	public Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
		return MultipleConnectionTransactionUtils.getTransaction(transactionDefinition);
	}

	public void commit(Transaction transaction) throws TransactionException {
		MultipleConnectionTransactionUtils.process((MultipleConnectionTransactionSynchronization) transaction);
	}

	public void rollback(Transaction transaction) throws TransactionException {
		MultipleConnectionTransactionUtils.rollback((MultipleConnectionTransactionSynchronization) transaction);
	}

}
