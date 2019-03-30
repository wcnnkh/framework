package scw.transaction.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.TransactionResource;
import scw.transaction.savepoint.MultipleSavepoint;
import scw.transaction.savepoint.Savepoint;

/**
 * 每个连接都是独立的事务
 * 
 * @author shuchaowen
 *
 */
public class MultipleConnectionResource implements TransactionResource {
	private Map<ConnectionFactory, ConnectionTransactionResource> connectionMap;
	private final TransactionDefinition transactionDefinition;
	private final boolean active;

	public MultipleConnectionResource(Transaction transaction) {
		this.transactionDefinition = transaction.getTransactionDefinition();
		this.active = transaction.isActive();
	}

	public Connection getConnection(ConnectionFactory connectionFactory) throws SQLException {
		ConnectionTransactionResource resource;
		if (connectionMap == null) {
			resource = new ConnectionTransactionResource(connectionFactory, transactionDefinition, active);
			connectionMap = new HashMap<ConnectionFactory, ConnectionTransactionResource>(4, 1);
			connectionMap.put(connectionFactory, resource);
		} else {
			resource = connectionMap.get(connectionFactory);
			if (resource == null) {
				resource = new ConnectionTransactionResource(connectionFactory, transactionDefinition, active);
				connectionMap.put(connectionFactory, resource);
			}
		}
		return resource.getConnection();
	}

	public void process() {
		if (connectionMap == null) {
			return;
		}

		Collection<ConnectionTransactionResource> resources = connectionMap.values();
		for (ConnectionTransactionResource resource : resources) {
			try {
				resource.process();
			} catch (Exception e) {
				resource.rollback();
				e.printStackTrace();
			} finally {
				resource.end();
			}
		}
	}

	public void rollback() {
	}

	public void end() {
	}

	public Savepoint createSavepoint() throws TransactionException {
		if (connectionMap == null) {
			return null;
		}

		return new MultipleSavepoint(connectionMap.values());
	}

}
