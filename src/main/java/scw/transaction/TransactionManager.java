package scw.transaction;

public interface TransactionManager {
	Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException;

	void commit(Transaction transaction) throws TransactionException;

	void rollback(Transaction transaction) throws TransactionException;
}
