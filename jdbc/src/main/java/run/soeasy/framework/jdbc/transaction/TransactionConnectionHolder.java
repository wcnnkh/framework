package run.soeasy.framework.jdbc.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import run.soeasy.framework.jdbc.ConnectionFactory;
import run.soeasy.framework.transaction.Isolation;
import run.soeasy.framework.transaction.Resource;
import run.soeasy.framework.transaction.Savepoint;
import run.soeasy.framework.transaction.Transaction;
import run.soeasy.framework.transaction.TransactionException;
import run.soeasy.framework.util.logging.LogManager;
import run.soeasy.framework.util.logging.Logger;

public class TransactionConnectionHolder implements Resource {
	private static Logger logger = LogManager.getLogger(TransactionConnectionHolder.class);
	private static final String SAVEPOINT_NAME_PREFIX = "SAVEPOINT_";
	private final Transaction transaction;
	private int savepointCounter;
	private final ConnectionFactory connectionFactory;
	private Connection connection;

	public TransactionConnectionHolder(Transaction transaction, ConnectionFactory connectionFactory) {
		this.transaction = transaction;
		this.connectionFactory = connectionFactory;
	}

	public boolean hasConnection() {
		return connection != null;
	}

	public Connection getConnection() throws SQLException {
		if (connection == null) {
			connection = connectionFactory.getConnection();
			connection.setAutoCommit(!transaction.isActive());

			if (transaction.getDefinition().isReadOnly()) {
				connection.setReadOnly(true);
			}

			Isolation isolation = transaction.getDefinition().getIsolation();
			if (isolation != Isolation.DEFAULT) {
				connection.setTransactionIsolation(isolation.getLevel());
			}

			connection = SqlTransactionUtils.conversionProxyConnection(connection);
		}
		return connection;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void close() {
		if (hasConnection()) {
			if (transaction.isActive()) {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					logger.error(e, "Failed to set autocommit");
				}
			}

			try {
				SqlTransactionUtils.closeProxyConnection(connection);
			} catch (SQLException e) {
				logger.error(e, "completion - " + connection);
			}
		}
	}

	public void rollback() {
		if (hasConnection()) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				logger.error(e, "rollback - " + connection);
			}
		}
	}

	public synchronized Savepoint createSavepoint() throws TransactionException {
		savepointCounter++;
		try {
			return new ConnectionSavepoint(getConnection(), SAVEPOINT_NAME_PREFIX + savepointCounter);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

	public void commit() throws Throwable {
		if (hasConnection()) {
			Connection connection = getConnection();
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
		}
	}
}
