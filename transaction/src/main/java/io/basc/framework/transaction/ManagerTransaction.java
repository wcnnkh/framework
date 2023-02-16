package io.basc.framework.transaction;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;

public final class ManagerTransaction implements Transaction {
	private final TransactionManager transactionManager;
	private final Transaction source;

	public ManagerTransaction(TransactionManager transactionManager, Transaction source) {
		Assert.requiredArgument(transactionManager != null, "transactionManager");
		Assert.requiredArgument(source != null, "source");
		this.transactionManager = transactionManager;
		this.source = source;
	}

	public Transaction getSource() {
		return source;
	}

	@Override
	public Savepoint createSavepoint() throws TransactionException {
		return source.createSavepoint();
	}

	@Override
	public Transaction getParent() {
		Transaction transaction = getParent();
		if (transaction == null) {
			return null;
		}

		if (transaction instanceof ManagerTransaction) {
			return transaction;
		}
		return new ManagerTransaction(transactionManager, transaction);
	}

	@Override
	public TransactionDefinition getDefinition() {
		return source.getDefinition();
	}

	@Override
	public Registration registerSynchronization(Synchronization synchronization) throws TransactionException {
		return source.registerSynchronization(synchronization);
	}

	@Override
	public <T> T getResource(Object name) {
		return source.getResource(name);
	}

	@Override
	public Registration registerResource(Object name, Object resource) throws TransactionException {
		return source.registerResource(name, resource);
	}

	@Override
	public boolean isRollbackOnly() {
		return source.isRollbackOnly();
	}

	@Override
	public void setRollbackOnly() throws TransactionException {
		source.setRollbackOnly();
	}

	@Override
	public boolean isNew() {
		return source.isNew();
	}

	@Override
	public boolean isActive() {
		return source.isActive();
	}

	@Override
	public boolean hasSavepoint() {
		return source.hasSavepoint();
	}

	@Override
	public TransactionStatus getStatus() {
		return source.getStatus();
	}

	@Override
	public boolean isCompleted() {
		return source.isCompleted();
	}

	@Override
	public void commit() throws Throwable {
		if (source instanceof ManagerTransaction) {
			source.commit();
		} else {
			transactionManager.commit(source);
		}
	}

	@Override
	public void rollback() {
		if (source instanceof ManagerTransaction) {
			source.rollback();
		} else {
			transactionManager.rollback(source);
		}
	}
}
