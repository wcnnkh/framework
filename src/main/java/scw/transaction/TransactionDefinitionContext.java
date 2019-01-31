package scw.transaction;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.jdbc.transaction.ConnectionHolder;
import scw.transaction.datasource.ConnectionFactory;
import scw.transaction.datasource.ConnectionProxy;
import scw.transaction.datasource.ConnectionProxyHandler;

public class TransactionDefinitionContext {
	private Map<ConnectionFactory, ConnectionHolder> connectionMap;
	private Propagation propagation;
	private TransactionDefinitionContext sup;
	private Map<ConnectionHolder, Savepoint> savepointMap;
	private boolean readOnly;

	public TransactionDefinitionContext(Propagation propagation, TransactionDefinitionContext sup) {
		this.propagation = propagation;
		this.sup = sup;
	}

	public Propagation getTransactionDefinition() {
		return propagation;
	}

	public void setTransactionDefinition(Propagation propagation) {
		this.propagation = propagation;
	}

	public TransactionDefinitionContext getSup() {
		return sup;
	}

	protected ConnectionHolder getTransactionConnection(ConnectionFactory connectionFactory) {
		return connectionMap == null ? null : connectionMap.get(connectionFactory);
	}

	private ConnectionHolder newConnectionHolder(ConnectionFactory connectionFactory)
			throws IllegalArgumentException, SQLException {
		ConnectionHolder connection;
		if (connectionMap == null) {
			connectionMap = new HashMap<ConnectionFactory, ConnectionHolder>();
			connection = connectionProxy(connectionFactory);
			connectionMap.put(connectionFactory, connection);
		} else {
			connection = connectionMap.get(connectionFactory);
			if (connection == null) {
				connection = connectionProxy(connectionFactory);
				connectionMap.put(connectionFactory, connection);
			}
		}
		return connection;
	}

	private ConnectionHolder connectionProxy(ConnectionFactory connectionFactory)
			throws IllegalArgumentException, SQLException {
		Connection connection = (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(),
				new Class<?>[] { ConnectionProxy.class },
				new ConnectionProxyHandler(connectionFactory.getConnection()));
		return new ConnectionHolder(connection);
	}

	public ConnectionHolder getConnectionHolder(ConnectionFactory connectionFactory)
			throws IllegalArgumentException, SQLException {
		ConnectionHolder connection = sup.getTransactionConnection(connectionFactory);
		switch (propagation) {
		case REQUIRED:
			if (connection == null || connection.getConnection().getAutoCommit()) {
				// 原来不存在事务新开一个事务
				connection = newConnectionHolder(connectionFactory);
				connection.getConnection().setAutoCommit(false);// 使用事务
			}
			break;
		case SUPPORTS:
			if (connection == null || connection.getConnection().getAutoCommit()) {
				connection = newConnectionHolder(connectionFactory);
			}
			break;
		case MANDATORY:
			if (connection == null || connection.getConnection().getAutoCommit()) {
				throw new TransactionException(propagation.name());
			}
			break;
		case REQUIRES_NEW:
			connection = newConnectionHolder(connectionFactory);
			connection.getConnection().setAutoCommit(false);
			break;
		case NOT_SUPPORTED:
			connection = newConnectionHolder(connectionFactory);
			break;
		case NEVER:
			if (connection != null && !connection.getConnection().getAutoCommit()) {
				throw new TransactionException(propagation.name());
			}
			break;
		case NESTED:
			if (connection != null && !connection.getConnection().getAutoCommit()) {// 如果存在事务设置一个保存点
				if (savepointMap == null) {
					savepointMap = new HashMap<ConnectionHolder, Savepoint>();
				}
				savepointMap.put(connection, connection.createSavePoint());
			} else {
				connection = newConnectionHolder(connectionFactory);
				connection.getConnection().setAutoCommit(false);
			}
			break;
		}
		return connection;
	}

	public void commit() throws SQLException {
		if (connectionMap != null) {
			for (Entry<ConnectionFactory, ConnectionHolder> entry : connectionMap.entrySet()) {
				entry.getValue().getConnection().commit();
			}
		}
	}

	public void rollback() {
		if (savepointMap != null) {
			for (Entry<ConnectionHolder, Savepoint> entry : savepointMap.entrySet()) {
				try {
					entry.getKey().getConnection().rollback(entry.getValue());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		if (connectionMap != null) {
			for (Entry<ConnectionFactory, ConnectionHolder> entry : connectionMap.entrySet()) {
				try {
					entry.getValue().getConnection().rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void close() {
		if (connectionMap != null) {
			for (Entry<ConnectionFactory, ConnectionHolder> entry : connectionMap.entrySet()) {
				try {
					entry.getValue().close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
