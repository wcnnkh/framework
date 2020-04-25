package scw.transaction.sql;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import scw.sql.ConnectionFactory;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

public abstract class SqlTransactionUtils {
	private SqlTransactionUtils() {
	};

	/**
	 * 获取一个当前事务的连接，如果不存在事务就返回可用的连接
	 * 
	 * @param connectionFactory
	 * @return
	 * @throws SQLException
	 */
	public static Connection getTransactionConnection(ConnectionFactory connectionFactory) throws SQLException {
		Transaction transaction = TransactionManager.getCurrentTransaction();
		if (transaction == null) {
			return connectionFactory.getConnection();
		}

		ConnectionTransactionResource resource = (ConnectionTransactionResource) transaction
				.getResource(connectionFactory);
		if (resource == null) {
			resource = new ConnectionTransactionResource(connectionFactory, transaction.getTransactionDefinition(),
					transaction.isActive());
			transaction.bindResource(connectionFactory, resource);
		}

		return resource.getConnection();
	}

	/**
	 * 获取一个多连接的事务管理器，但这几个连接的事务是非同步的
	 * 
	 * @param connectionFactory
	 * @return
	 * @throws SQLException
	 */
	public static Connection getMultipleTransactionConnection(ConnectionFactory connectionFactory) throws SQLException {
		Transaction transaction = TransactionManager.getCurrentTransaction();
		if (transaction == null) {
			return connectionFactory.getConnection();
		}

		MultipleConnectionResource resource = (MultipleConnectionResource) transaction
				.getResource(MultipleConnectionResource.class);
		if (resource == null) {
			resource = new MultipleConnectionResource(transaction);
			transaction.bindResource(MultipleConnectionResource.class, resource);
		}
		return resource.getConnection(connectionFactory);
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

	/**
	 * 真实的关闭代理连接
	 * 
	 * @param connection
	 * @throws SQLException
	 */
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
