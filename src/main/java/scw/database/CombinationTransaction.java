package scw.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import scw.common.transaction.Transaction;
import scw.common.transaction.TransactionCollection;

/**
 * 组合事务
 * 
 * @author shuchaowen
 *
 */
public final class CombinationTransaction implements Transaction {
	private HashMap<ConnectionSource, SQLTransaction> dbSqlMap = new HashMap<ConnectionSource, SQLTransaction>(2, 1);
	private TransactionCollection transactionCollection = new TransactionCollection(4);

	public void addSql(ConnectionSource db, Collection<SQL> sqls) {
		if (sqls == null || db == null) {
			return;
		}

		SQLTransaction sqlTransaction = dbSqlMap.getOrDefault(db, new SQLTransaction(db));
		for (SQL s : sqls) {
			sqlTransaction.addSql(s);
		}
		dbSqlMap.put(db, sqlTransaction);
	}

	public void addTransaction(Transaction collection) {
		transactionCollection.add(collection);
	}

	public void begin() throws Exception {
		for (Entry<ConnectionSource, SQLTransaction> entry : dbSqlMap.entrySet()) {
			transactionCollection.add(entry.getValue());
		}
		transactionCollection.begin();
	}

	public void clear() {
		if (dbSqlMap != null) {
			for (Entry<ConnectionSource, SQLTransaction> entry : dbSqlMap.entrySet()) {
				entry.getValue().clear();
			}
		}

		if (transactionCollection != null) {
			transactionCollection.clear();
		}
	}

	public void process() throws Exception {
		if (transactionCollection != null) {
			transactionCollection.process();
		}
	}

	public void end() throws Exception {
		if (transactionCollection != null) {
			transactionCollection.end();
		}
	}

	public void rollback() throws Exception {
		if (transactionCollection != null) {
			transactionCollection.rollback();
		}
	}
}
