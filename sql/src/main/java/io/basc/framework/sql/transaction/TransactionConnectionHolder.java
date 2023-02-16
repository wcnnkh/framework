package io.basc.framework.sql.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.sql.ConnectionFactory;
import io.basc.framework.transaction.Isolation;
import io.basc.framework.transaction.Savepoint;
import io.basc.framework.transaction.SavepointManager;
import io.basc.framework.transaction.Synchronization;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionException;
import io.basc.framework.transaction.TransactionStatus;

public class TransactionConnectionHolder implements Synchronization, SavepointManager {
	private static Logger logger = LoggerFactory.getLogger(TransactionConnectionHolder.class);
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
			connection.setAutoCommit(transaction.isActive());

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

	public void complete() {
		if (hasConnection()) {
			if (transaction.isActive()) {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
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

	public Savepoint createSavepoint() throws TransactionException {
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

	@Override
	public void beforeCompletion() throws Throwable {
		commit();
	}

	@Override
	public void afterCompletion(TransactionStatus status) {
		if (status.equals(TransactionStatus.ROLLING_BACK)) {
			rollback();
		}

		if (status.equals(TransactionStatus.COMPLETED)) {
			complete();
		}
	}
}
