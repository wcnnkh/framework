package scw.transaction;

public interface TransactionResource extends TransactionSynchronization{

	Savepoint createSavepoint() throws TransactionException;
}
