package shuchaowen.core.db;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.Context;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.transaction.AbstractTransaction;
import shuchaowen.core.transaction.SQLTransaction;
import shuchaowen.core.transaction.Transaction;
import shuchaowen.core.transaction.TransactionCollection;
import shuchaowen.core.util.Logger;

/**
 * 数据库封装核心类，用于处理数据库事务
 * 
 * @author shuchaowen
 */
public final class TransactionContext extends Context<ThreadLocalDBTransaction> {
	private boolean cacheEnable;
	private boolean debug = false;

	/**
	 * 理论上一个应用程序只要一个事务管理器
	 */
	private static TransactionContext instance = new TransactionContext();

	/**
	 * 获取一个事务上下文的单例
	 * 
	 * @return
	 */
	public static TransactionContext getInstance() {
		return instance;
	}

	public TransactionContext() {
		this(false, true);
	}

	public TransactionContext(boolean debug, boolean cacheEnable) {
		this.debug = debug;
		this.cacheEnable = cacheEnable;
	}

	/**
	 * 不参与正在进行的事务，单独执行
	 */
	public void forceExecute(ConnectionPool db, Collection<SQL> sqls) {
		if (sqls == null || db == null) {
			throw new NullPointerException();
		}

		if (debug) {
			for (SQL s : sqls) {
				Logger.debug("SQL", DBUtils.getSQLId(s));
			}
		}

		DBUtils.execute(db, sqls);
	}

	/**
	 * 此方式还不成熟，在要求不太高的情况下可以试试
	 * 
	 * @param transaction
	 */
	public void execute(Transaction transaction) {
		if (transaction == null) {
			return;
		}

		ThreadLocalDBTransaction localDBTransaction = getValue();
		if (localDBTransaction == null || localDBTransaction.isAutoCommit()) {// 如果未使用事务
			try {
				AbstractTransaction.transaction(transaction);
			} catch (Throwable e) {
				throw new ShuChaoWenRuntimeException(e);
			}
		} else {
			localDBTransaction.addTransaction(transaction);
		}
	}

	public void execute(ConnectionPool db, SQL... sqls) {
		execute(db, Arrays.asList(sqls));
	}

	/**
	 * 提交一个SQL语句，如果已经开启了事务会使用事务的方式，如果未开启事务就会直接执行
	 * 
	 * @param connectionOrigin
	 * @param sql
	 */
	public void execute(ConnectionPool db, Collection<SQL> sqls) {
		if (db == null || sqls == null || sqls.isEmpty()) {
			return;
		}

		if (debug) {
			for (SQL s : sqls) {
				Logger.debug("SQL", DBUtils.getSQLId(s));
			}
		}

		ThreadLocalDBTransaction localDBTransaction = getValue();
		if (localDBTransaction == null || localDBTransaction.isAutoCommit()) {// 如果未使用事务
			SQLTransaction sqlTransaction = new SQLTransaction(db);
			for (SQL sql : sqls) {
				sqlTransaction.addSql(sql);
			}

			try {
				sqlTransaction.execute();
			} catch (Exception e) {
				throw new ShuChaoWenRuntimeException(e);
			}
		} else {
			localDBTransaction.addSql(db, sqls);
		}
	}

	public void execute(ConnectionPool db, Collection<SQL> sqls, Transaction transaction) {
		if (debug) {
			if (sqls != null) {
				for (SQL s : sqls) {
					Logger.debug("SQL", DBUtils.getSQLId(s));
				}
			}
		}

		ThreadLocalDBTransaction localDBTransaction = getValue();
		if (localDBTransaction == null || localDBTransaction.isAutoCommit()) {// 如果未使用事务
			TransactionCollection transactionCollection = new TransactionCollection(2);
			if (db != null && sqls != null && !sqls.isEmpty()) {
				SQLTransaction sqlTransaction = new SQLTransaction(db);
				for (SQL sql : sqls) {
					sqlTransaction.addSql(sql);
				}

				transactionCollection.add(sqlTransaction);
			}

			if (transaction != null) {
				transactionCollection.add(transaction);
			}

			try {
				AbstractTransaction.transaction(transactionCollection);
			} catch (Exception e) {
				throw new ShuChaoWenRuntimeException(e);
			}
		} else {
			localDBTransaction.addSql(db, sqls);
			if (transaction != null) {
				localDBTransaction.addTransaction(transaction);
			}
		}
	}

	public boolean isCacheEnable() {
		return cacheEnable;
	}

	/**
	 * 是否在同一个事务内开启查询缓存
	 * 
	 * @param cacheEnable
	 */
	public void setCacheEnable(boolean cacheEnable) {
		this.cacheEnable = cacheEnable;
	}

	public ResultSet select(ConnectionPool db, SQL sql) {
		if (debug) {
			Logger.debug("SQL", DBUtils.getSQLId(sql));
		}

		if (cacheEnable) {
			ThreadLocalDBTransaction localDBTransaction = getValue();
			if (localDBTransaction == null || localDBTransaction.isAutoCommit()) {// 如果未使用事务
				return DBUtils.select(db, sql);
			} else {
				return localDBTransaction.select(db, sql);
			}
		} else {
			return DBUtils.select(db, sql);
		}
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	protected void firstBegin() {
		ThreadLocalDBTransaction sqlTransaction = getValue();
		if (sqlTransaction == null) {
			sqlTransaction = new ThreadLocalDBTransaction();
			setValue(sqlTransaction);
		}
		sqlTransaction.beginTransaction();
	}

	@Override
	protected void lastCommit(){
		ThreadLocalDBTransaction localDBTransaction = getValue();
		if (localDBTransaction != null) {
			localDBTransaction.commitTransaction();
		}
	}
}

class ThreadLocalDBTransaction extends AbstractTransaction {
	private HashMap<ConnectionPool, SQLTransaction> dbSqlMap = new HashMap<ConnectionPool, SQLTransaction>();
	private Map<ConnectionPool, Map<String, ResultSet>> cacheMap = new HashMap<ConnectionPool, Map<String, ResultSet>>();
	private TransactionCollection transactionCollection = new TransactionCollection();
	private boolean isAutoCommit = true;// 是否是自动提交

	public boolean isAutoCommit() {
		return isAutoCommit;
	}

	void addTransaction(Transaction collection) {
		transactionCollection.add(collection);
	}

	void beginTransaction() {
		if (isAutoCommit) {// 如果原来是自动提交，现在改为手动提交，为了防止脏数据应该先清除一遍
			reset();
			isAutoCommit = false;
		}
	}

	void commitTransaction(){
		if (isAutoCommit) {
			throw new ShuChaoWenRuntimeException("transaction status error autoCommit[" + isAutoCommit + "]");
		}

		// 应该要提交事务了了
		try {
			execute();
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		} finally {
			isAutoCommit = true;
			reset();
		}
	}

	ResultSet select(ConnectionPool db, SQL sql) {
		if (isAutoCommit) {
			return DBUtils.select(db, sql);
		} else {
			ResultSet resultSet = null;
			String id = DBUtils.getSQLId(sql);
			Map<String, ResultSet> map = cacheMap.getOrDefault(id, new HashMap<String, ResultSet>());
			if (map.containsKey(id)) {
				return map.get(id);
			} else {
				resultSet = DBUtils.select(db, sql);
				map.put(id, resultSet);
				cacheMap.put(db, map);
				return resultSet;
			}
		}
	}

	/**
	 * 重置
	 */
	private void reset() {
		cacheMap.clear();
		transactionCollection.clear();
		for (Entry<ConnectionPool, SQLTransaction> entry : dbSqlMap.entrySet()) {
			entry.getValue().clear();
		}
	}

	void addSql(ConnectionPool db, Collection<SQL> sqls) {
		if (sqls == null || db == null) {
			return;
		}

		SQLTransaction sqlTransaction = dbSqlMap.getOrDefault(db, new SQLTransaction(db));
		for (SQL s : sqls) {
			sqlTransaction.addSql(s);
		}
		dbSqlMap.put(db, sqlTransaction);
	}

	public void begin() throws Exception {
		for (Entry<ConnectionPool, SQLTransaction> entry : dbSqlMap.entrySet()) {
			transactionCollection.add(entry.getValue());
		}
		transactionCollection.begin();
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
