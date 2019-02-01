package scw.transaction.support;

import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;

public abstract class TransactionInfo {
	private TransactionDefinition transactionDefinition;
	private Transaction transaction;
	private TransactionInfo parent;
	private Object savepoint;

	public TransactionInfo(TransactionDefinition transactionDefinition, Transaction transaction,
			TransactionInfo parent) {
		this.transactionDefinition = transactionDefinition;
		this.transaction = transaction;
		this.parent = parent;
	}

	public TransactionDefinition getTransactionDefinition() {
		return transactionDefinition;
	}

	protected Transaction getTransaction() {
		return transaction;
	}

	protected abstract Transaction newTransaction(Transaction oldTransaction,
			TransactionDefinition transactionDefinition, boolean txActive) throws TransactionException;

	public Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
		Transaction transaction = parent.getTransaction();
		switch (transactionDefinition.getPropagation()) {
		case REQUIRED:
			if (transaction == null || !transaction.isActive()) {
				transaction = newTransaction(transaction, transactionDefinition, true);
			}
			break;
		case SUPPORTS:
			if (transaction == null) {
				transaction = newTransaction(transaction, transactionDefinition, false);
			}
			break;
		case MANDATORY:
			if (transaction == null || !transaction.isActive()) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			}
			break;
		case REQUIRES_NEW:
			transaction = newTransaction(transaction, transactionDefinition, true);
			break;
		case NOT_SUPPORTED:
			transaction = newTransaction(transaction, transactionDefinition, false);
			break;
		case NEVER:
			if (transaction != null && transaction.isActive()) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			}
			break;
		case NESTED:
			if (transaction != null && transaction.isActive()) {
				savepoint = transaction.createSavepoint();
			} else {
				transaction = newTransaction(transaction, transactionDefinition, true);
			}
			break;
		}
		return transaction;
	}

	public void rollback() {
		if (savepoint != null) {
			transaction.rollbackToSavepoint(savepoint);
		}
	}

	public void release() {
		if (savepoint != null) {
			transaction.releaseSavepoint(savepoint);
		}
	}
}
