package shuchaowen.core.db;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.Context;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.transaction.AbstractTransaction;
import shuchaowen.core.transaction.CombinationTransaction;
import shuchaowen.core.transaction.SQLTransaction;
import shuchaowen.core.transaction.Transaction;
import shuchaowen.core.transaction.TransactionCollection;
import shuchaowen.core.util.Logger;

/**
 * 数据库封装核心类，用于处理数据库事务
 * 
 * @author shuchaowen
 */
public final class TransactionContext extends Context<ThreadLocalTransaction> {
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

		ThreadLocalTransaction threadLocalTransaction = getValue();
		if (threadLocalTransaction == null || threadLocalTransaction.isAutoCommit()) {// 如果未使用事务
			try {
				AbstractTransaction.transaction(transaction);
			} catch (Throwable e) {
				throw new ShuChaoWenRuntimeException(e);
			}
		} else {
			threadLocalTransaction.addTransaction(transaction);
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

		ThreadLocalTransaction localDBTransaction = getValue();
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

		ThreadLocalTransaction threadLocalTransaction = getValue();
		if (threadLocalTransaction == null || threadLocalTransaction.isAutoCommit()) {// 如果未使用事务
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
			threadLocalTransaction.addSql(db, sqls);
			if (transaction != null) {
				threadLocalTransaction.addTransaction(transaction);
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
			ThreadLocalTransaction threadLocalTransaction = getValue();
			if (threadLocalTransaction == null || threadLocalTransaction.isAutoCommit()) {// 如果未使用事务
				return DBUtils.select(db, sql);
			} else {
				return threadLocalTransaction.select(db, sql);
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
	
	public void setAutoCommit(boolean autoCommit){
		ThreadLocalTransaction threadLocalTransaction = getValue();
		if (threadLocalTransaction == null) {
			throw new ShuChaoWenRuntimeException("请先开启当前线程的事务");
		}
		threadLocalTransaction.setAutoCommit(autoCommit);
	}

	@Override
	protected void firstBegin() {
		ThreadLocalTransaction threadLocalTransaction = getValue();
		if (threadLocalTransaction == null) {
			threadLocalTransaction = new ThreadLocalTransaction(debug);
			setValue(threadLocalTransaction);
		}
		threadLocalTransaction.beginTransaction();
	}

	@Override
	protected void lastCommit() {
		ThreadLocalTransaction threadLocalTransaction = getValue();
		if (threadLocalTransaction != null) {
			try {
				threadLocalTransaction.commitTransaction();
			} catch (Exception e) {
				throw new ShuChaoWenRuntimeException(e);
			}
		}
	}
}

final class ThreadLocalTransaction extends AbstractTransaction {
	private CombinationTransaction combinationTransaction = new CombinationTransaction();
	private Map<ConnectionPool, Map<String, ResultSet>> cacheMap = new HashMap<ConnectionPool, Map<String, ResultSet>>();
	private boolean autoCommit = true;// 是否是自动提交
	private final boolean debug;

	public ThreadLocalTransaction(boolean debug) {
		this.debug = debug;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	void addTransaction(Transaction collection) {
		combinationTransaction.addTransaction(collection);
	}

	void beginTransaction() {
		if (debug) {
			Logger.debug("transaction-context", "begin transaction");
		}

		if (autoCommit) {// 如果原来是自动提交，现在改为手动提交，为了防止脏数据应该先清除一遍
			reset();
			autoCommit = false;
		}
	}

	void commitTransaction() throws Exception {
		// 应该要提交事务了了
		try {
			execute();
		} finally {
			if (debug) {
				Logger.debug("transaction-context", "end transaction");
			}

			autoCommit = true;
			reset();
		}
	}

	ResultSet select(ConnectionPool db, SQL sql) {
		if (autoCommit) {
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
		combinationTransaction.clear();
	}

	void addSql(ConnectionPool db, Collection<SQL> sqls) {
		combinationTransaction.addSql(db, sqls);
	}

	public void begin() throws Exception {
		combinationTransaction.begin();
	}

	public void process() throws Exception {
		combinationTransaction.process();
	}

	public void end() throws Exception {
		combinationTransaction.end();
	}

	public void rollback() throws Exception {
		combinationTransaction.rollback();
	}
}
