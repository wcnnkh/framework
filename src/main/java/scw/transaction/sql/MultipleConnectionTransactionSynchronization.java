package scw.transaction.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.sql.ConnectionFactory;
import scw.transaction.AbstractTransaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.support.TransactionLifeCycleCollection;
import scw.transaction.support.TransactionSynchronization;
import scw.transaction.support.TransactionSynchronizationCollection;
import scw.transaction.support.TransactionSynchronizationLifeCycle;

public class MultipleConnectionTransactionSynchronization extends
		AbstractTransaction implements TransactionSynchronization {
	private Map<ConnectionFactory, ConnectionTransaction> cstsMap;
	private TransactionDefinition transactionDefinition;
	private TransactionSynchronizationCollection tsc;
	private TransactionLifeCycleCollection tlcc;
	private MultipleConnectionTransactionSynchronization parent;
	private Object savepoint;

	public MultipleConnectionTransactionSynchronization(
			TransactionDefinition transactionDefinition, boolean active) {
		super(active);
		setNewTransaction(true);
		this.transactionDefinition = transactionDefinition;
	}

	/**
	 * 创建一个旧的
	 * 
	 * @param mcts
	 */
	public MultipleConnectionTransactionSynchronization(
			MultipleConnectionTransactionSynchronization mcts) {
		super(mcts.isActive());
		setNewTransaction(false);
		this.parent = mcts;
	}

	public Connection getConnection(ConnectionFactory connectionFactory)
			throws SQLException {
		if (parent != null) {
			return parent.getConnection(connectionFactory);
		}

		ConnectionTransaction csts;
		if (cstsMap == null) {
			csts = new ConnectionTransaction(
					connectionFactory, transactionDefinition, isActive());
			cstsMap.put(connectionFactory, csts);
		} else {
			csts = cstsMap.get(connectionFactory);
			if (csts == null) {
				csts = new ConnectionTransaction(
						connectionFactory, transactionDefinition, isActive());
				cstsMap.put(connectionFactory, csts);
			}
		}
		return csts.getConnection();
	}

	public ConnectionTransaction getConnectionSavepointTransactionSynchronization(
			ConnectionFactory connectionFactory) {
		if (parent != null) {
			return parent
					.getConnectionSavepointTransactionSynchronization(connectionFactory);
		}

		if (cstsMap == null) {
			return null;
		}

		return cstsMap.get(connectionFactory);
	}

	public void createTempSavePoint() {
		this.savepoint = createSavepoint();
	}

	private TransactionSynchronizationLifeCycle tslc;

	public void begin() throws TransactionException {
		if (isNewTransaction()) {
			if (tslc != null) {
				return;
			}

			TransactionSynchronizationCollection stsc = new TransactionSynchronizationCollection();
			if (tsc != null) {
				stsc.add(tsc);
			}

			if (cstsMap != null) {
				for (Entry<ConnectionFactory, ConnectionTransaction> entry : cstsMap
						.entrySet()) {
					stsc.add(entry.getValue());
				}
			}

			tslc = new TransactionSynchronizationLifeCycle(stsc, tlcc);
		}
	}

	public void process() throws TransactionException {
		if (tslc != null) {
			tslc.process();
		}
	}

	public void rollback() throws TransactionException {
		if (hasSavepoint()) {
			rollbackToSavepoint(savepoint);
		}

		if (tslc != null) {
			tslc.rollback();
		}
	}

	public void end() {
		if (hasSavepoint()) {
			releaseSavepoint(savepoint);
		}

		if (tslc != null) {
			tslc.end();
		}
	}

	public void rollbackToSavepoint(Object savepoint)
			throws TransactionException {
		if (savepoint == null) {
			return;
		}

		if (savepoint instanceof HashMap) {
			throw new TransactionException("savepoint类型错误, 无法回滚到此结点");
		}

		@SuppressWarnings("unchecked")
		HashMap<ConnectionFactory, Object> savepointMap = (HashMap<ConnectionFactory, Object>) savepoint;
		for (Entry<ConnectionFactory, Object> entry : savepointMap.entrySet()) {
			ConnectionTransaction csts = cstsMap
					.get(entry.getKey());
			csts.releaseSavepoint(savepointMap);
		}
	}

	public void releaseSavepoint(Object savepoint) throws TransactionException {
		if (savepoint == null) {
			return;
		}

		if (savepoint instanceof HashMap) {
			throw new TransactionException(
					"savepoint类型错误, 无法releaseSavepoint到此结点");
		}

		@SuppressWarnings("unchecked")
		HashMap<ConnectionFactory, Object> savepointMap = (HashMap<ConnectionFactory, Object>) savepoint;
		for (Entry<ConnectionFactory, Object> entry : savepointMap.entrySet()) {
			ConnectionTransaction csts = cstsMap
					.get(entry.getKey());
			csts.releaseSavepoint(savepointMap);
		}
	}

	public Object createSavepoint() throws TransactionException {
		if (cstsMap == null) {
			return null;
		}

		HashMap<ConnectionFactory, Object> savepointMap = new HashMap<ConnectionFactory, Object>();
		for (Entry<ConnectionFactory, ConnectionTransaction> entry : cstsMap
				.entrySet()) {
			savepointMap
					.put(entry.getKey(), entry.getValue().createSavepoint());
		}
		return savepointMap;
	}

	public boolean hasSavepoint() {
		return savepoint != null;
	}

	public Object getSavepoint() {
		return savepoint;
	}
}
