package scw.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.common.Logger;
import scw.common.transaction.AbstractTransaction;
import scw.common.transaction.Transaction;
import scw.database.result.ResultSet;

final class ThreadLocalTransaction extends AbstractTransaction {
	private CombinationTransaction combinationTransaction;
	private Map<ConnectionSource, Map<String, ResultSet>> cacheMap;
	private boolean debug;
	private boolean selectCache;

	public ThreadLocalTransaction(boolean debug, boolean selectCache) {
		this.debug = debug;
		this.selectCache = selectCache;
	}

	void addTransaction(Transaction collection) {
		if (combinationTransaction == null) {
			combinationTransaction = new CombinationTransaction();
		}
		combinationTransaction.addTransaction(collection);
	}

	private ResultSet realSelect(ConnectionSource connectionSource, SQL sql) {
		if (debug) {
			Logger.debug("SQL", DataBaseUtils.getSQLId(sql));
		}
		return DataBaseUtils.select(connectionSource, sql);
	}

	public boolean isSelectCache() {
		return selectCache;
	}

	public void setSelectCache(boolean selectCache) {
		this.selectCache = selectCache;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	ResultSet select(ConnectionSource connectionSource, SQL sql) {
		if (selectCache) {
			ResultSet resultSet;
			String id = DataBaseUtils.getSQLId(sql);
			if (cacheMap == null) {
				cacheMap = new HashMap<ConnectionSource, Map<String, ResultSet>>(2, 1);
				resultSet = realSelect(connectionSource, sql);
				Map<String, ResultSet> map = new HashMap<String, ResultSet>();
				map.put(id, resultSet);
				cacheMap.put(connectionSource, map);
			} else {
				Map<String, ResultSet> map = cacheMap.getOrDefault(connectionSource, new HashMap<String, ResultSet>());
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
		} else {
			return realSelect(connectionSource, sql);
		}
	}

	void clearSelectCache() {
		if (cacheMap != null) {
			cacheMap.clear();
		}
	}

	void addSql(ConnectionSource db, Collection<SQL> sqls) {
		if (combinationTransaction == null) {
			combinationTransaction = new CombinationTransaction();
		}
		combinationTransaction.addSql(db, sqls);
	}

	public void begin() throws Exception {
		if (combinationTransaction != null) {
			combinationTransaction.begin();
		}
	}

	public void process() throws Exception {
		if (combinationTransaction != null) {
			combinationTransaction.process();
		}
	}

	public void end() throws Exception {
		if (combinationTransaction != null) {
			combinationTransaction.end();
		}
	}

	public void rollback() throws Exception {
		if (combinationTransaction != null) {
			combinationTransaction.rollback();
		}
	}
}
