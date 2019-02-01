package scw.transaction.synchronization;

import scw.transaction.Transaction;
import scw.transaction.TransactionException;

public abstract class AbstractTransaction implements Transaction {

	private final boolean active;
	private boolean newTransaction = true;

	public AbstractTransaction(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isNewTransaction() {
		return newTransaction;
	}

	public void setNewTransaction(boolean newTransaction) {
		this.newTransaction = newTransaction;
	}

	protected abstract void rollback() throws TransactionException;

	protected abstract void commit() throws TransactionException;
}
