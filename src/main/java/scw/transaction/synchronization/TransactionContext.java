package scw.transaction.synchronization;

import scw.transaction.TransactionDefinition;

public class TransactionContext {
	private TransactionDefinition transactionDefinition;

	public TransactionDefinition getTransactionDefinition() {
		return transactionDefinition;
	}

	public void setTransactionDefinition(TransactionDefinition transactionDefinition) {
		this.transactionDefinition = transactionDefinition;
	}
}
