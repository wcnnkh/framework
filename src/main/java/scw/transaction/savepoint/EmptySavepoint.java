package scw.transaction.savepoint;

import scw.transaction.TransactionException;

public final class EmptySavepoint implements Savepoint {

	public void rollback() throws TransactionException {
	}

	public void release() throws TransactionException {
	}

}
