package scw.transaction;

import scw.transaction.support.TransactionSynchronization;

public abstract class AbstractTransaction implements Transaction, TransactionSynchronization {

	private boolean active;
	private boolean newTransaction = true;

	public AbstractTransaction(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isNewTransaction() {
		return newTransaction;
	}

	public void setNewTransaction(boolean newTransaction) {
		this.newTransaction = newTransaction;
	}
}
