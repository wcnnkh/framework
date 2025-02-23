package io.basc.framework.transaction;

/**
 * 事务管理器
 * 
 * @see ThreadLocalTransactionManager
 * @author wcnnkh
 *
 */
public interface TransactionManager {
	Transaction getTransaction();

	default boolean hasTransaction() {
		return getTransaction() != null;
	}

	default Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
		Transaction transaction = getTransaction();
		switch (transactionDefinition.getPropagation()) {
		case REQUIRED:
			if (transaction == null) {
				transaction = new StandardTransaction(transaction, transactionDefinition, true);
			} else {
				transaction = new StandardTransaction(transaction, transactionDefinition);
			}
			break;
		case SUPPORTS:
			if (transaction == null) {
				transaction = new StandardTransaction(transaction, transactionDefinition, false);
			} else {
				transaction = new StandardTransaction(transaction, transactionDefinition);
			}
			break;
		case MANDATORY:
			if (transaction == null) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			} else {
				if (transaction.isActive()) {
					transaction = new StandardTransaction(transaction, transactionDefinition);
				} else {
					throw new TransactionException(transactionDefinition.getPropagation().name());
				}
			}
			break;
		case REQUIRES_NEW:
			transaction = new StandardTransaction(transaction, transactionDefinition, true);
			break;
		case NOT_SUPPORTED:
			transaction = new StandardTransaction(transaction, transactionDefinition, false);
			break;
		case NEVER:
			if (transaction == null) {
				transaction = new StandardTransaction(transaction, transactionDefinition, false);
			} else {
				if (transaction.isActive()) {
					throw new TransactionException(transactionDefinition.getPropagation().name());
				} else {
					transaction = new StandardTransaction(transaction, transactionDefinition);
				}
			}
			break;
		case NESTED:
			if (transaction == null) {
				transaction = new StandardTransaction(transaction, transactionDefinition, true);
			} else {
				if (transaction.isActive()) {
					Savepoint savepoint = transaction.createSavepoint();
					transaction = new StandardTransaction(transaction, transactionDefinition, savepoint, true);
				} else {
					transaction = new StandardTransaction(transaction, transactionDefinition);
				}
			}
			break;
		}
		return transaction;
	}

	void commit(Transaction transaction) throws Throwable, TransactionException;

	void rollback(Transaction transaction) throws TransactionException;
}