package io.basc.framework.tx;

import io.basc.framework.util.Assert;

public final class FacadeTransactionManager implements TransactionManager {
	private final TransactionManager transactionManager;

	public FacadeTransactionManager(TransactionManager transactionManager) {
		Assert.requiredArgument(transactionManager != null, "transactionManager");
		this.transactionManager = transactionManager;
	}

	@Override
	public Transaction getTransaction() {
		Transaction transaction = transactionManager.getTransaction();
		if (transaction == null) {
			return null;
		}

		return transaction instanceof FacadeTransaction ? transaction
				: new FacadeTransaction(transactionManager, transaction);
	}

	@Override
	public Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
		Transaction transaction = transactionManager.getTransaction(transactionDefinition);
		if (transaction == null) {
			return null;
		}
		return transaction instanceof FacadeTransaction ? transaction
				: new FacadeTransaction(transactionManager, transaction);
	}

	@Override
	public boolean hasTransaction() {
		return transactionManager.hasTransaction();
	}

	@Override
	public void commit(Transaction transaction) throws Throwable, TransactionException {
		if (transaction instanceof FacadeTransaction) {
			transaction.commit();
			return;
		}
		transactionManager.commit(transaction);
	}

	@Override
	public void rollback(Transaction transaction) throws TransactionException {
		if (transaction instanceof FacadeTransaction) {
			transaction.rollback();
			return;
		}
		transactionManager.rollback(transaction);
	}

	public static TransactionManager of(TransactionManager transactionManager) {
		if (transactionManager == null) {
			return transactionManager;
		}

		return transactionManager instanceof FacadeTransactionManager ? transactionManager
				: new FacadeTransactionManager(transactionManager);

	}

	@Override
	public String toString() {
		return transactionManager.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof FacadeTransactionManager) {
			return this.transactionManager.equals(((FacadeTransactionManager) obj).transactionManager);
		}
		return this.transactionManager.equals(obj);
	}

	@Override
	public int hashCode() {
		return transactionManager.hashCode();
	}
}
