package scw.transaction.sql;

import java.util.Map;
import java.util.Map.Entry;

import scw.sql.ConnectionFactory;
import scw.transaction.TransactionException;
import scw.transaction.synchronization.TransactionSynchronization;
import scw.transaction.synchronization.TransactionSynchronizationCollection;

public class MultipleConnectionTransactionSynchronization implements TransactionSynchronization {
	private Map<ConnectionFactory, ConnectionTransactionSynchronization> connectionSynchoronizationMap;
	private TransactionSynchronizationCollection tsc;

	public void begin() throws TransactionException {
		tsc = new TransactionSynchronizationCollection();

		if (connectionSynchoronizationMap == null) {
			return;
		}

		for (Entry<ConnectionFactory, ConnectionTransactionSynchronization> entry : connectionSynchoronizationMap
				.entrySet()) {
			tsc.add(entry.getValue());
		}
		tsc.begin();
	}

	public void commit() throws TransactionException {
		tsc.commit();
	}

	public void rollback() throws TransactionException {
		tsc.rollback();
	}

	public void end() {
		tsc.end();
	}
}
