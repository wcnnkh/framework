package scw.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import scw.common.transaction.AbstractTransaction;
import scw.common.transaction.Transaction;
import scw.common.transaction.TransactionCollection;
import scw.common.utils.CollectionUtils;

/**
 * 事务上下文隔离
 * 
 * @author shuchaowen
 *
 */
public class TransactionContextQuarantine {
	private LinkedList<TransactionLifeCycle> lifeCycles;
	private LinkedList<Transaction> transactionList;
	private LinkedList<TransactionSql> sqlList;
	private ContextConfig config;

	public TransactionContextQuarantine(ContextConfig config) {
		this.config = config;
	}

	public void commit(TransactionContextQuarantine contextQuarantine) {
		List<Transaction> tList = contextQuarantine.getTransactionList();
		if (tList != null) {
			if (transactionList == null) {
				transactionList = new LinkedList<Transaction>();
			}

			Iterator<Transaction> iterator = tList.iterator();
			while (iterator.hasNext()) {
				Transaction transaction = iterator.next();
				transactionList.add(transaction);
				iterator.remove();
			}
		}

		LinkedList<TransactionSql> commitSqlList = contextQuarantine.getSqlList();
		if (commitSqlList != null) {
			if (sqlList == null) {
				sqlList = new LinkedList<TransactionSql>();
			}

			Iterator<TransactionSql> iterator = commitSqlList.iterator();
			while (iterator.hasNext()) {
				TransactionSql sql = iterator.next();
				sqlList.add(sql);
				iterator.remove();
			}
		}

		LinkedList<TransactionLifeCycle> transactionLifeCycles = contextQuarantine.getLifeCycles();
		if (transactionLifeCycles != null) {
			if (lifeCycles == null) {
				lifeCycles = new LinkedList<TransactionLifeCycle>();
			}

			Iterator<TransactionLifeCycle> iterator = transactionLifeCycles.iterator();
			while (iterator.hasNext()) {
				TransactionLifeCycle lifeCycle = iterator.next();
				lifeCycles.add(lifeCycle);
				iterator.remove();
			}
		}
	}

	public void addSql(ConnectionSource connectionSource, SQL sql) {
		if (sql == null) {
			return;
		}

		if (sqlList == null) {
			sqlList = new LinkedList<TransactionSql>();
		}

		sqlList.add(new TransactionSql(connectionSource, sql));
	}

	public void addSql(ConnectionSource connectionSource, Collection<SQL> sqls) {
		if (CollectionUtils.isEmpty(sqls)) {
			return;
		}

		if (sqlList == null) {
			sqlList = new LinkedList<TransactionSql>();
		}

		sqlList.add(new TransactionSql(connectionSource, sqls));
	}

	public void addTransaction(Transaction transaction) {
		if (transaction == null) {
			return;
		}

		if (transactionList == null) {
			transactionList = new TransactionCollection();
		}
		transactionList.add(transaction);
	}

	public void addTransactionLifeCycleListenin(TransactionLifeCycle lifeCycle) {
		if (lifeCycle == null) {
			return;
		}

		if (lifeCycles == null) {
			lifeCycles = new LinkedList<TransactionLifeCycle>();
		}

		lifeCycles.add(lifeCycle);
	}

	public ContextConfig getConfig() {
		return config;
	}

	public LinkedList<Transaction> getTransactionList() {
		return transactionList;
	}

	public LinkedList<TransactionSql> getSqlList() {
		return sqlList;
	}

	public LinkedList<TransactionLifeCycle> getLifeCycles() {
		return lifeCycles;
	}

	public void execute() {
		TransactionCollection transactionCollection = null;
		if (transactionList != null) {
			transactionCollection = new TransactionCollection(transactionList);
		}

		if (sqlList != null) {
			Iterator<TransactionSql> iterator = sqlList.iterator();
			HashMap<ConnectionSource, SQLTransaction> dbSqlMap = new HashMap<ConnectionSource, SQLTransaction>(2, 1);
			while (iterator.hasNext()) {
				TransactionSql transactionSql = iterator.next();
				SQLTransaction sqlTransaction = dbSqlMap.get(transactionSql.getConnectionSource());
				if (sqlTransaction == null) {
					sqlTransaction = new SQLTransaction(transactionSql.getConnectionSource());
					dbSqlMap.put(transactionSql.getConnectionSource(), sqlTransaction);
				}

				Iterator<SQL> sqlIterator = transactionSql.getSqls().iterator();
				while (sqlIterator.hasNext()) {
					SQL sql = sqlIterator.next();
					if (sql == null) {
						continue;
					}
					sqlTransaction.addSql(sql);
				}
			}

			if (transactionCollection == null) {
				transactionCollection = new TransactionCollection();
			}

			for (Entry<ConnectionSource, SQLTransaction> entry : dbSqlMap.entrySet()) {
				transactionCollection.add(entry.getValue());
			}
		}

		if (lifeCycles == null) {
			if (transactionCollection != null) {
				AbstractTransaction.transaction(transactionCollection);
			}
		} else {
			Iterator<TransactionLifeCycle> iterator = lifeCycles.iterator();
			try {
				while (iterator.hasNext()) {
					TransactionLifeCycle lifeCycle = iterator.next();
					lifeCycle.before();
				}

				if (transactionCollection != null) {
					AbstractTransaction.transaction(transactionCollection);
				}

				iterator = lifeCycles.iterator();
				while (iterator.hasNext()) {
					TransactionLifeCycle lifeCycle = iterator.next();
					lifeCycle.after();
				}
			} finally {
				iterator = lifeCycles.iterator();
				while (iterator.hasNext()) {
					TransactionLifeCycle lifeCycle = iterator.next();
					lifeCycle.complete();
				}
			}
		}
	}
}
