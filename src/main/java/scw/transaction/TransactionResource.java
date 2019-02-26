package scw.transaction;

import scw.transaction.savepoint.Savepoint;

public abstract class TransactionResource {

	public abstract Savepoint createSavepoint() throws TransactionException;

	protected abstract void process() throws TransactionException;

	protected abstract void rollback() throws TransactionException;

	protected abstract void end();
}
