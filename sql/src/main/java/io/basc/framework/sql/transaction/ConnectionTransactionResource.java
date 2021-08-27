package io.basc.framework.sql.transaction;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.sql.ConnectionFactory;
import io.basc.framework.transaction.DefaultTransaction;
import io.basc.framework.transaction.Isolation;
import io.basc.framework.transaction.TransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;

public final class ConnectionTransactionResource extends AbstractConnectionTransactionResource {
	private static Logger logger = LoggerFactory.getLogger(ConnectionTransactionResource.class);
	private final ConnectionFactory connectionFactory;
	private Connection connection;

	public ConnectionTransactionResource(ConnectionFactory connectionFactory,
			TransactionDefinition transactionDefinition, boolean active) {
		super(transactionDefinition, active);
		this.connectionFactory = connectionFactory;
	}

	public ConnectionTransactionResource(ConnectionFactory connectionFactory, DefaultTransaction transaction) {
		super(transaction.getDefinition(), transaction.isActive());
		this.connectionFactory = connectionFactory;
	}

	public boolean hasConnection() {
		return connection != null;
	}

	public Connection getConnection() throws SQLException {
		if (connection == null) {
			connection = connectionFactory.getConnection();
			connection.setAutoCommit(!isActive());

			if (getTransactionDefinition().isReadOnly()) {
				connection.setReadOnly(getTransactionDefinition().isReadOnly());
			}

			Isolation isolation = getTransactionDefinition().getIsolation();
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
			if (isActive()) {
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
}
