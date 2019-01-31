package scw.transaction.support;

import scw.transaction.Transaction;

public abstract class AbstractTransaction implements Transaction {
	private boolean newTransaction;

	public AbstractTransaction(boolean newTransaction) {
		this.newTransaction = newTransaction;
	}

	public boolean isNewTransaction() {
		return newTransaction;
	}

	/**
	 * 是否已经开始
	 * 
	 * @return
	 */
	abstract boolean isBegin();
}
