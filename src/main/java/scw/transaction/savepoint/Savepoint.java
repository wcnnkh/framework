package scw.transaction.savepoint;

import scw.transaction.TransactionException;

public interface Savepoint {

	void rollback() throws TransactionException;

	void release() throws TransactionException;
}
