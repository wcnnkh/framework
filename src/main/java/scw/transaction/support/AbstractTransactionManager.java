package scw.transaction.support;

import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.TransactionManager;

public abstract class AbstractTransactionManager implements TransactionManager {
	private TransactionDefinition transactionDefinition;
	private AbstractTransaction transaction;
	private AbstractTransactionManager parent;

	public AbstractTransactionManager(TransactionDefinition transactionDefinition, AbstractTransaction transaction,
			AbstractTransactionManager parent) {
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

	protected abstract Transaction newTransaction(TransactionDefinition transactionDefinition)
			throws TransactionException;

	public Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
		Transaction transaction = parent.getTransaction();
		switch (transactionDefinition.getPropagation()) {
		case REQUIRED:
			if (transaction == null) {
				transaction = newTransaction(transactionDefinition);
			}
			break;
		case SUPPORTS:
			if (transaction == null) {
				transaction = newTransaction(transactionDefinition);
			}
			break;
		case MANDATORY:
			if (transaction == null) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			}
			break;
		case REQUIRES_NEW:
			transaction = newTransaction(transactionDefinition);
			break;
		case NOT_SUPPORTED:
			transaction = newTransaction(transactionDefinition);
			break;
		case NEVER:
			if (transaction != null) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			}
			break;
		case NESTED:
			if (transaction != null) {
				// transaction.createSavepoint();
			} else {
				transaction = newTransaction(transactionDefinition);
			}
			break;
		}
		return transaction;
	}
}
