package run.soeasy.framework.jdbc.transaction;

import run.soeasy.framework.jdbc.ConnectionFactory;
import run.soeasy.framework.transaction.Transaction;
import run.soeasy.framework.transaction.TransactionUtils;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

public final class SqlTransactionUtils {
	private SqlTransactionUtils() {
	};

	public static Connection getTransactionConnection(ConnectionFactory connectionFactory) throws SQLException {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction == null) {
			return connectionFactory.getConnection();
		}

		TransactionConnectionHolder resource = transaction.getResource(connectionFactory);
		if (resource == null) {
			resource = new TransactionConnectionHolder(transaction, connectionFactory);
			transaction.registerResource(connectionFactory, resource);
		}
		return resource.getConnection();
	}

	public static Connection conversionProxyConnection(Connection connection) {
		if (connection == null) {
			return connection;
		}

		if (connection instanceof ConnectionProxy) {
			return connection;
		}

		return connection = (ConnectionProxy) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(),
				new Class<?>[] { ConnectionProxy.class }, new UnableToCloseConnectionProxyHandler(connection));
	}

	public static void closeProxyConnection(Connection connection) throws SQLException {
		if (connection == null) {
			return;
		}

		if (connection instanceof ConnectionProxy) {
			((ConnectionProxy) connection).getTargetConnection().close();
		} else {
			connection.close();
		}
	}
}
