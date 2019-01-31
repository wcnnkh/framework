package scw.transaction;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.jdbc.ConnectionFactory;

public class TransactionResource {
	private Map<Object, Object> attributeMap;
	/**
	 * 同步事务调用
	 */
	private TransactionSynchronizationCollection transactionSynchronizationCollection;

	/**
	 * 事务配置
	 */
	private TransactionConfig transactionConfig;
	private Map<ConnectionFactory, Connection> connectionMap;

	public TransactionResource(TransactionConfig transactionConfig) {
		this.transactionConfig = transactionConfig;
	}

	public void merge(TransactionResource resource) {
		if (attributeMap != null) {
			for (Entry<Object, Object> entry : resource.getAttributeMap().entrySet()) {
				resource.setAttribute(entry.getKey(), entry.getValue());
			}
		}

		if (transactionSynchronizationCollection != null) {
			resource.addTransactionSynchronization(transactionSynchronizationCollection);
		}
	}

	public void setAttribute(Object key, Object value) {
		if (attributeMap == null) {
			attributeMap = new HashMap<Object, Object>();
		}
		attributeMap.put(key, value);
	}

	public Object getAttribute(Object key) {
		if (attributeMap == null) {
			return null;
		}
		return attributeMap.get(key);
	}

	@SuppressWarnings("unchecked")
	public Map<Object, Object> getAttributeMap() {
		return attributeMap == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(attributeMap);
	}

	public void clear() {
		attributeMap = null;
		transactionSynchronizationCollection = null;
	}

	public void addTransactionSynchronization(TransactionSynchronization transactionSynchronization) {
		if (transactionSynchronizationCollection == null) {
			transactionSynchronizationCollection = new TransactionSynchronizationCollection();
		}
		transactionSynchronizationCollection.add(transactionSynchronization);
	}

	public void executeTransactionSynchronization() {
		if (transactionSynchronizationCollection != null) {
			TransactionSynchronizationUitls.execute(transactionSynchronizationCollection);
		}
	}

	public TransactionConfig getTransactionConfig() {
		return transactionConfig;
	}
}
