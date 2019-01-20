package scw.database;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.Logger;
import scw.common.transaction.AbstractTransaction;
import scw.common.transaction.Transaction;
import scw.common.transaction.TransactionCollection;
import scw.database.result.ResultSet;

public class TransactionContextInfo {
	private Map<ConnectionSource, Map<String, ResultSet>> cacheMap;// 查询缓存
	private LinkedList<TransactionSql> sqlList;// sql事务缓存
	private TransactionCollection transactionCollection;// tcc缓存
	private ContextConfig config;
	private int index = 0;// 开始标记

	private LinkedList<TransactionContextQuarantine> configList = new LinkedList<TransactionContextQuarantine>();// 事务隔离

	public TransactionContextInfo(ContextConfig config) {
		this.config = config;
	}

	public ContextConfig getTransactionContextConfig() {
		return config;
	}

	public TransactionContextQuarantine getTransactionContextQuarantine() {
		return configList.getLast();
	}

	public ResultSet select(ConnectionSource connectionSource, SQL sql) {
		ResultSet resultSet;
		String id = DataBaseUtils.getSQLId(sql);
		if (cacheMap == null) {
			cacheMap = new HashMap<ConnectionSource, Map<String, ResultSet>>(2,
					1);
			resultSet = realSelect(connectionSource, sql);
			Map<String, ResultSet> map = new HashMap<String, ResultSet>();
			map.put(id, resultSet);
			cacheMap.put(connectionSource, map);
		} else {
			Map<String, ResultSet> map = cacheMap.getOrDefault(
					connectionSource, new HashMap<String, ResultSet>());
			if (map == null) {
				resultSet = realSelect(connectionSource, sql);
				map = new HashMap<String, ResultSet>();
				map.put(id, resultSet);
				cacheMap.put(connectionSource, map);
			} else if (map.containsKey(id)) {
				resultSet = map.get(id);
			} else {
				resultSet = realSelect(connectionSource, sql);
				map.put(id, resultSet);
				cacheMap.put(connectionSource, map);
			}
		}
		return resultSet;
	}

	private ResultSet realSelect(ConnectionSource connectionSource, SQL sql) {
		if (getTransactionContextConfig().isDebug()) {
			Logger.debug(this.getClass().getName(), DataBaseUtils.getSQLId(sql));
		}
		return DataBaseUtils.select(connectionSource, sql);
	}

	public void begin() {
		if (index == 0) {
			configList.add(new TransactionContextQuarantine(config));
		} else {
			TransactionContextQuarantine lastConfig = configList.getLast();
			configList.add(new TransactionContextQuarantine(lastConfig
					.getConfig()));
		}
		index++;
	}

	public void commit() {// 把当前级别的事务汇总到事务缓存中
		TransactionContextQuarantine lastConfig = configList.getLast();
		List<Transaction> tList = lastConfig.getTransactionList();
		if (tList != null) {
			if (transactionCollection == null) {
				transactionCollection = new TransactionCollection();
			}

			Iterator<Transaction> iterator = tList.iterator();
			while (iterator.hasNext()) {
				Transaction transaction = iterator.next();
				if (transaction != null) {
					transactionCollection.add(transaction);
				}
				iterator.remove();
			}
		}

		LinkedList<TransactionSql> commitSqlList = lastConfig.getSqlList();
		if (commitSqlList != null) {
			if (sqlList == null) {
				sqlList = new LinkedList<TransactionSql>();
			}

			Iterator<TransactionSql> iterator = commitSqlList.iterator();
			while (iterator.hasNext()) {
				TransactionSql sql = iterator.next();
				if (sql != null) {
					sqlList.add(sql);
				}
				iterator.remove();
			}
		}
	}

	public void end() {
		index--;
		configList.removeLast();
		if (index == 0) {// 最后一次了,执行吧
			if (sqlList != null) {
				HashMap<ConnectionSource, SQLTransaction> dbSqlMap = new HashMap<ConnectionSource, SQLTransaction>(
						2, 1);
				Iterator<TransactionSql> iterator = sqlList.iterator();
				while (iterator.hasNext()) {
					TransactionSql transactionSql = iterator.next();
					SQLTransaction sqlTransaction = dbSqlMap.get(transactionSql
							.getConnectionSource());
					if (sqlTransaction == null) {
						sqlTransaction = new SQLTransaction(
								transactionSql.getConnectionSource());
						dbSqlMap.put(transactionSql.getConnectionSource(),
								sqlTransaction);
					}

					Iterator<SQL> sqlIterator = transactionSql.getSqls()
							.iterator();
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

				for (Entry<ConnectionSource, SQLTransaction> entry : dbSqlMap
						.entrySet()) {
					transactionCollection.add(entry.getValue());
				}
			}

			if (transactionCollection != null) {
				AbstractTransaction.transaction(transactionCollection);
			}
		}
	}
}
