package io.basc.framework.transaction;

public interface SavepointManager {
	Savepoint createSavepoint() throws TransactionException;
}
