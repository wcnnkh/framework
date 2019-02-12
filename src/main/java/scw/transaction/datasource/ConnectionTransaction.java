package scw.transaction.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import scw.sql.ConnectionFactory;
import scw.transaction.Isolation;
import scw.transaction.NotSupportTransactionException;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.synchronization.AbstractTransaction;

public class ConnectionTransaction extends AbstractTransaction {
	private static final String SAVEPOINT_NAME_PREFIX = "SAVEPOINT_";

	private final ConnectionFactory connectionFactory;
	private final TransactionDefinition transactionDefinition;
	private Connection connection;
	private int savepointCounter;

	public ConnectionTransaction(ConnectionFactory connectionFactory, TransactionDefinition transactionDefinition,
			boolean active) {
		super(active);
		this.connectionFactory = connectionFactory;
		this.transactionDefinition = transactionDefinition;
	}

	public Connection getConnection() throws SQLException {
		if (connection == null) {
			Connection connection = connectionFactory.getConnection();
			connection.setAutoCommit(isActive());

			if (transactionDefinition.isReadOnly()) {
				connection.setReadOnly(transactionDefinition.isReadOnly());
			}

			Isolation isolation = transactionDefinition.getIsolation();
			if (isolation != Isolation.DEFAULT) {
				connection.setTransactionIsolation(isolation.getLevel());
			}
		}
		return connection;
	}

	public Object createSavepoint() throws TransactionException {
		savepointCounter++;
		try {
			return getConnection().setSavepoint(SAVEPOINT_NAME_PREFIX + savepointCounter);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

	public void rollbackToSavepoint(Object savepoint) throws TransactionException {
		if (!(savepoint instanceof Savepoint)) {
			throw new NotSupportTransactionException("not suppert savepoint");
		}
		try {
			getConnection().rollback((Savepoint) savepoint);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

	public void releaseSavepoint(Object savepoint) throws TransactionException {
		if (!(savepoint instanceof Savepoint)) {
			throw new NotSupportTransactionException("not suppert savepoint");
		}
		try {
			getConnection().releaseSavepoint((Savepoint) savepoint);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

	public int getSavepointCounter() {
		return savepointCounter;
	}

	public boolean hasSavepoint() {
		return savepointCounter > 0;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void commit() throws TransactionException {
		if (connection != null) {
			try {
				connection.commit();
			} catch (SQLException e) {
				close();
			}
		}
	}

	private void close() throws TransactionException {
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new TransactionException(e);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new TransactionException(e);
			}
		}
	}

	public void rollback() throws TransactionException {
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				close();
			}
		}
	}
}
