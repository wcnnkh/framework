package scw.transaction.synchronization;

import scw.transaction.Transaction;

public abstract class AbstractTransaction implements Transaction, TransactionSynchronization {

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
}
