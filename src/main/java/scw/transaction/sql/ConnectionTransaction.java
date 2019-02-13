package scw.transaction.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import scw.sql.ConnectionFactory;
import scw.sql.SqlUtils;
import scw.transaction.Isolation;
import scw.transaction.NotSupportTransactionException;
import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.synchronization.TransactionSynchronization;

public class ConnectionTransaction implements Transaction, TransactionSynchronization {
	private static final String SAVEPOINT_NAME_PREFIX = "SAVEPOINT_";

	private final ConnectionFactory connectionFactory;
	private final TransactionDefinition transactionDefinition;
	private final boolean newTransaction;
	private final boolean active;
	private Connection connection;
	private int savepointCounter;

	public ConnectionTransaction(ConnectionFactory connectionFactory, TransactionDefinition transactionDefinition,
			boolean active) {
		this.active = active;
		this.newTransaction = true;
		this.connectionFactory = connectionFactory;
		this.transactionDefinition = transactionDefinition;
	}

	public Connection getConnection() throws SQLException {
		if (connection == null) {
			Connection connection = SqlUtils.newProxyConnection(connectionFactory);
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
		return savepointCounter != 0;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public boolean isNewTransaction() {
		return newTransaction;
	}

	public boolean isActive() {
		return active;
	}

	public void begin() throws TransactionException {
		if (connection == null) {
			try {
				connection = getConnection();
			} catch (SQLException e) {
				throw new TransactionException(e);
			}
		}
	}

	public void end() throws TransactionException {
		if (connection != null) {
			if (active) {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			try {
				SqlUtils.closeProxyConnection(connection);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void rollback() throws TransactionException {
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new TransactionException(e);
			}
		}
	}

	public void commit() throws TransactionException {
		if (connection != null) {
			try {
				connection.commit();
			} catch (SQLException e) {
				throw new TransactionException(e);
			}
		}
	}
}
