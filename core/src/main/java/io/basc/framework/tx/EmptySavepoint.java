package io.basc.framework.tx;

final class EmptySavepoint implements Savepoint {

	public void rollback() throws TransactionException {
	}

	public void release() throws TransactionException {
	}

}
