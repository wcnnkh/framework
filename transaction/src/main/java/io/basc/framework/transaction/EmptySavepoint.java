package io.basc.framework.transaction;

final class EmptySavepoint implements Savepoint {

	public void rollback() throws TransactionException {
	}

	public void release() throws TransactionException {
	}

}
