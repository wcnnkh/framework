package scw.transaction.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import scw.transaction.TransactionDefinition;
import scw.transaction.support.AbstractTransaction;

public class ConnectionTransaction extends AbstractTransaction {
	private static final String SAVE_POINT_PREFIX = "SAVEPOINT_";

	private final ConnectionFactory connectionFactory;
	private final boolean autoCommit;
	private final TransactionDefinition transactionDefinition;
	private Connection connection;
	private int savePointCount;

	public ConnectionTransaction(boolean newTransaction,
			ConnectionFactory connectionFactory, boolean autoCommit,
			TransactionDefinition transactionDefinition) {
		super(newTransaction);
		this.connectionFactory = connectionFactory;
		this.transactionDefinition = transactionDefinition;
		this.autoCommit = autoCommit;
	}

	public boolean isCompleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasTransaction() {
		return autoCommit;
	}

	public Connection getConnection() throws SQLException {
		if (connection == null) {
			Connection connection = connectionFactory.getConnection();
			connection.setAutoCommit(autoCommit);

			if (transactionDefinition.isReadOnly()) {
				connection.setReadOnly(transactionDefinition.isReadOnly());
			}
		}
		return connection;
	}

	@Override
	public Object createSavepoint() {
		return null;
	}

}
