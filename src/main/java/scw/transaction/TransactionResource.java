package scw.transaction;

import scw.transaction.savepoint.Savepoint;

public interface TransactionResource extends TransactionSynchronization{

	Savepoint createSavepoint() throws TransactionException;
}
