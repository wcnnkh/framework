package scw.transaction;

public interface Savepoint {

	void rollback() throws TransactionException;

	void release() throws TransactionException;
}
