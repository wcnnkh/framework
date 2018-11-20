package shuchaowen.core.db;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.Context;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
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
		this(false);
	}

	public TransactionContext(boolean debug) {
		this.debug = debug;
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
		if (threadLocalTransaction == null) {// 如果未使用事务
			AbstractTransaction.transaction(transaction);
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
		if (localDBTransaction == null) {// 如果未使用事务
			SQLTransaction sqlTransaction = new SQLTransaction(db);
			for (SQL sql : sqls) {
				sqlTransaction.addSql(sql);
			}

			sqlTransaction.execute();
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
		if (threadLocalTransaction == null) {// 如果未使用事务
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

			AbstractTransaction.transaction(transactionCollection);
		} else {
			threadLocalTransaction.addSql(db, sqls);
			if (transaction != null) {
				threadLocalTransaction.addTransaction(transaction);
			}
		}
	}

	public ResultSet select(ConnectionPool db, SQL sql) {
		ThreadLocalTransaction threadLocalTransaction = getValue();
		if (threadLocalTransaction == null) {// 如果未使用事务
			if (debug) {
				Logger.debug("SQL", DBUtils.getSQLId(sql));
			}
			return DBUtils.select(db, sql);
		} else {
			return threadLocalTransaction.select(db, sql);
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
		ThreadLocalTransaction threadLocalTransaction = getValue();
		if (threadLocalTransaction == null) {
			threadLocalTransaction = new ThreadLocalTransaction(debug);
			setValue(threadLocalTransaction);
		}
	}

	@Override
	protected void lastCommit() {
		ThreadLocalTransaction threadLocalTransaction = getValue();
		if (threadLocalTransaction != null) {
			threadLocalTransaction.execute();
		}
	}
}

final class ThreadLocalTransaction extends AbstractTransaction {
	private CombinationTransaction combinationTransaction;
	private Map<ConnectionPool, Map<String, ResultSet>> cacheMap;
	private final boolean debug;

	public ThreadLocalTransaction(boolean debug) {
		this.debug = debug;
	}

	void addTransaction(Transaction collection) {
		if(combinationTransaction == null){
			combinationTransaction = new CombinationTransaction();
		}
		combinationTransaction.addTransaction(collection);
	}
	
	private ResultSet realSelect(ConnectionPool db, SQL sql){
		if (debug) {
			Logger.debug("SQL", DBUtils.getSQLId(sql));
		}
		return DBUtils.select(db, sql);
	}

	ResultSet select(ConnectionPool db, SQL sql) {
		ResultSet resultSet;
		String id = DBUtils.getSQLId(sql);
		if(cacheMap == null){
			cacheMap = new HashMap<ConnectionPool, Map<String,ResultSet>>(2, 1);
			resultSet = realSelect(db, sql);
			Map<String, ResultSet> map = new HashMap<String, ResultSet>();
			map.put(id, resultSet);
			cacheMap.put(db, map);
		}else{
			Map<String, ResultSet> map = cacheMap.getOrDefault(id, new HashMap<String, ResultSet>());
			if(map == null){
				resultSet = realSelect(db, sql);
				map = new HashMap<String, ResultSet>();
				map.put(id, resultSet);
				cacheMap.put(db, map);
			} else if (map.containsKey(id)) {
				resultSet = map.get(id);
			} else {
				resultSet = realSelect(db, sql);
				map.put(id, resultSet);
				cacheMap.put(db, map);
			}
		}
		return resultSet;
	}

	void addSql(ConnectionPool db, Collection<SQL> sqls) {
		if(combinationTransaction == null){
			combinationTransaction = new CombinationTransaction();
		}
		combinationTransaction.addSql(db, sqls);
	}

	public void begin() throws Exception {
		if(combinationTransaction != null){
			combinationTransaction.begin();
		}
	}

	public void process() throws Exception {
		if(combinationTransaction != null){
			combinationTransaction.process();
		}
	}

	public void end() throws Exception {
		if(combinationTransaction != null){
			combinationTransaction.end();
		}
	}

	public void rollback() throws Exception {
		if(combinationTransaction != null){
			combinationTransaction.rollback();
		}
	}
}
