package scw.transaction.datasource;

import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.synchronization.AbstractTransaction;
import scw.transaction.synchronization.AbstractTransactionManager;

public class ConnectionTransactionManager extends AbstractTransactionManager {
	private final ConnectionFactory connectionFactory;

	public ConnectionTransactionManager(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	public AbstractTransaction newTransaction(AbstractTransaction parent, TransactionDefinition transactionDefinition,
			boolean active) throws TransactionException {
		return new ConnectionTransaction(connectionFactory, transactionDefinition, active);
	}
}
