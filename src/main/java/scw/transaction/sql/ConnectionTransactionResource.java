package scw.transaction.sql;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import scw.sql.ConnectionFactory;
import scw.transaction.Isolation;
import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;

public final class ConnectionTransactionResource extends AbstractConnectionTransactionResource {
	private final ConnectionFactory connectionFactory;
	private Connection connection;

	public ConnectionTransactionResource(ConnectionFactory connectionFactory,
			TransactionDefinition transactionDefinition, boolean active) {
		super(transactionDefinition, active);
		this.connectionFactory = connectionFactory;
	}

	public ConnectionTransactionResource(ConnectionFactory connectionFactory, Transaction transaction) {
		super(transaction.getTransactionDefinition(), transaction.isActive());
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

			connection = (ConnectionProxy) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(),
					new Class<?>[] { ConnectionProxy.class }, new UnableToCloseConnectionProxyHandler(connection));
		}
		return connection;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void end() {
		if (hasConnection()) {
			try {
				connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (isActive()) {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			try {
				if (connection instanceof ConnectionProxy) {
					((ConnectionProxy) connection).getTargetConnection().close();
				} else {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void rollback() {
		if (hasConnection()) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new TransactionException(e);
			}
		}
	}
}
