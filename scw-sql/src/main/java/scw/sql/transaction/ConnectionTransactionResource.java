package scw.sql.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.sql.ConnectionFactory;
import scw.transaction.DefaultTransaction;
import scw.transaction.Isolation;
import scw.transaction.TransactionDefinition;

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

	public void completion() {
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
