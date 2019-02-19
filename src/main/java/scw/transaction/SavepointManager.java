package scw.transaction;

public interface SavepointManager {

	Object createSavepoint() throws TransactionException;

	void rollbackToSavepoint(Object savepoint) throws TransactionException;

	void releaseSavepoint(Object savepoint) throws TransactionException;
}
