package scw.transaction.tcc;

import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.TransactionManager;

public class TccTransactionContextManager implements TransactionManager {
	private final ThreadLocal<TccTransactionContext> LOCAL = new ThreadLocal<TccTransactionContext>();

	public Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
		TccTransactionContext tccTransactionContext = LOCAL.get();
		return null;
	}

	public void commit(Transaction transaction) throws TransactionException {
		// TODO Auto-generated method stub

	}

	public void rollback(Transaction transaction) throws TransactionException {
	}
}
