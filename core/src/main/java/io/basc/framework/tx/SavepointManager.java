package io.basc.framework.tx;

public interface SavepointManager {
	Savepoint createSavepoint() throws TransactionException;
}
