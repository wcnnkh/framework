package io.basc.framework.transaction;

import io.basc.framework.util.Assert;

public final class FacadeTransaction implements Transaction {
	private final TransactionManager transactionManager;
	private final Transaction source;

	public FacadeTransaction(TransactionManager transactionManager, Transaction source) {
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

		if (transaction instanceof FacadeTransaction) {
			return transaction;
		}
		return new FacadeTransaction(transactionManager, transaction);
	}

	@Override
	public TransactionDefinition getDefinition() {
		return source.getDefinition();
	}

	@Override
	public void registerSynchronization(Synchronization synchronization) throws TransactionException {
		source.registerSynchronization(synchronization);
	}

	@Override
	public <T> T getResource(Object name) {
		return source.getResource(name);
	}

	@Override
	public void registerResource(Object name, Object resource) throws TransactionException {
		source.registerResource(name, resource);
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
	public Status getStatus() {
		return source.getStatus();
	}

	@Override
	public void commit() throws Throwable {
		if (source instanceof FacadeTransaction) {
			source.commit();
		} else {
			transactionManager.commit(source);
		}
	}

	@Override
	public void rollback() {
		if (source instanceof FacadeTransaction) {
			source.rollback();
		} else {
			transactionManager.rollback(source);
		}
	}

	@Override
	public String toString() {
		return source.toString();
	}

	@Override
	public int hashCode() {
		return source.hashCode();
	}

	@Override
	public boolean hasParent() {
		return source.hasParent();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof FacadeTransaction) {
			return this.source.equals(((FacadeTransaction) obj).source);
		}

		return this.source.equals(obj);
	}

	@Override
	public void close() {
		if (source.getStatus().isCompleted()) {
			return;
		}

		source.close();
	}
}
