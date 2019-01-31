package scw.transaction.support;

import scw.common.transaction.exception.TransactionException;
import scw.transaction.Transaction;

public abstract class AbstractTransaction implements Transaction {
	private boolean newTransaction;

	public AbstractTransaction(boolean newTransaction) {
		this.newTransaction = newTransaction;
	}

	public boolean isNewTransaction() {
		return newTransaction;
	}

	public abstract boolean hasTransaction();

	public abstract Object createSavepoint() throws TransactionException;
}
