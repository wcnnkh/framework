package scw.transaction;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.jdbc.ConnectionFactory;
import scw.jdbc.ConnectionProxy;
import scw.jdbc.ConnectionProxyHandler;

public class TransactionContext {
	/**
	 * 最后汇总的资源
	 */
	private TransactionResource resources;
	// 事务连接
	private Map<ConnectionFactory, Connection> connectionMap;
	// 资源隔离
	private LinkedList<TransactionResource> resourcesQuarantine = new LinkedList<TransactionResource>();

	private LinkedList<TransactionDefinitionContext> tx = new LinkedList<TransactionDefinitionContext>();
	
	public TransactionContext(TransactionConfig transactionConfig) {
		resources = new TransactionResource(transactionConfig);
	}
	
	public void begin(Propagation propagation){
		
	}
	
	protected void before() {
		resourcesQuarantine.add(new TransactionResource(resources.getTransactionConfig()));
	}

	protected void after() {
		resources.merge(resourcesQuarantine.getLast());
		resources.clear();
	}

	protected void complete() {
		resourcesQuarantine.removeLast();
		if (isLastComplete()) {
			resources.executeTransactionSynchronization();
		}
	}

	protected boolean isLastComplete() {
		return resourcesQuarantine.size() == 0;
	}

	public TransactionResource getResource() {
		return resourcesQuarantine.getLast();
	}

	public Connection getConnection(ConnectionFactory connectionFactory) throws SQLException {
		Connection connection;
		if (connectionMap == null) {
			connectionMap = new HashMap<ConnectionFactory, Connection>();
			connection = connectionFactory.getConnection();
			connection = (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(),
					new Class<?>[] { ConnectionProxy.class }, new ConnectionProxyHandler(connection));
		} else {
			connection = connectionMap.get(connectionFactory);
			if (connection == null) {
				connection = connectionFactory.getConnection();
				connection = (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(),
						new Class<?>[] { ConnectionProxy.class }, new ConnectionProxyHandler(connection));
				connectionMap.put(connectionFactory, connection);
			}
		}
		return connection;
	}
}
