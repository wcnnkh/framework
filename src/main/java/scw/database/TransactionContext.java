package scw.database;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.common.Context;
import scw.common.Logger;
import scw.common.transaction.AbstractTransaction;
import scw.common.transaction.Transaction;
import scw.common.transaction.TransactionCollection;
import scw.database.result.ResultSet;

/**
 * 数据库封装核心类，用于处理数据库事务
 * 
 * @author shuchaowen
 */
public final class TransactionContext extends Context<ThreadLocalTransaction> {
	private volatile boolean debug = false;

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
	public void forceExecute(ConnectionSource connectionSource, Collection<SQL> sqls) {
		if (sqls == null || connectionSource == null) {
			throw new NullPointerException();
		}

		if (isDebug()) {
			for (SQL s : sqls) {
				Logger.debug("SQL", DataBaseUtils.getSQLId(s));
			}
		}

		DataBaseUtils.execute(connectionSource, sqls);
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

	public void execute(ConnectionSource connectionSource, SQL... sqls) {
		execute(connectionSource, Arrays.asList(sqls));
	}

	/**
	 * 提交一个SQL语句，如果已经开启了事务会使用事务的方式，如果未开启事务就会直接执行
	 * 
	 * @param connectionOrigin
	 * @param sql
	 */
	public void execute(ConnectionSource connectionSource, Collection<SQL> sqls) {
		if (connectionSource == null || sqls == null || sqls.isEmpty()) {
			return;
		}

		ThreadLocalTransaction localDBTransaction = getValue();
		if (localDBTransaction == null) {// 如果未使用事务
			if (debug) {
				for (SQL s : sqls) {
					Logger.debug("SQL", DataBaseUtils.getSQLId(s));
				}
			}
			
			SQLTransaction sqlTransaction = new SQLTransaction(connectionSource);
			for (SQL sql : sqls) {
				sqlTransaction.addSql(sql);
			}

			sqlTransaction.execute();
		} else {
			if (localDBTransaction.isDebug()) {
				for (SQL s : sqls) {
					Logger.debug("SQL", DataBaseUtils.getSQLId(s));
				}
			}
			
			localDBTransaction.addSql(connectionSource, sqls);
		}
	}
	
	public void execute(ConnectionSource connectionSource, Collection<SQL> sqls, Transaction transaction) {
		ThreadLocalTransaction threadLocalTransaction = getValue();
		if (threadLocalTransaction == null) {// 如果未使用事务
			if (debug) {
				if (sqls != null) {
					for (SQL s : sqls) {
						Logger.debug("SQL", DataBaseUtils.getSQLId(s));
					}
				}
			}
			
			TransactionCollection transactionCollection = new TransactionCollection(2);
			if (connectionSource != null && sqls != null && !sqls.isEmpty()) {
				SQLTransaction sqlTransaction = new SQLTransaction(connectionSource);
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
			if (threadLocalTransaction.isDebug()) {
				if (sqls != null) {
					for (SQL s : sqls) {
						Logger.debug("SQL", DataBaseUtils.getSQLId(s));
					}
				}
			}
			
			threadLocalTransaction.addSql(connectionSource, sqls);
			if (transaction != null) {
				threadLocalTransaction.addTransaction(transaction);
			}
		}
	}

	/**
	 * @param connectionSource
	 * @param sql
	 * @return ResultSet不可能为空
	 */
	public ResultSet select(ConnectionSource connectionSource, SQL sql) {
		ThreadLocalTransaction threadLocalTransaction = getValue();
		if (threadLocalTransaction == null) {// 如果未使用事务
			if (debug) {
				Logger.debug("SQL", DataBaseUtils.getSQLId(sql));
			}
			return DataBaseUtils.select(connectionSource, sql);
		} else {
			return threadLocalTransaction.select(connectionSource, sql);
		}
	}
	
	public boolean isDebug(){
		ThreadLocalTransaction threadLocalTransaction = getValue();
		return threadLocalTransaction == null? debug:threadLocalTransaction.isDebug();
	}

	public void setDebug(boolean debug) {
		ThreadLocalTransaction threadLocalTransaction = getValue();
		if(threadLocalTransaction != null){
			threadLocalTransaction.setDebug(debug);
		}
	}
	
	/**
	 * 设置全局的DEBUG状态
	 * @param debug
	 */
	public void setGlobalDebug(boolean debug){
		this.debug = debug;
	}

	@Override
	protected void firstBegin() {
		setValue(new ThreadLocalTransaction(debug));
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
	private Map<ConnectionSource, Map<String, ResultSet>> cacheMap;
	private boolean debug;

	public ThreadLocalTransaction(boolean debug) {
		this.debug = debug;
	}

	void addTransaction(Transaction collection) {
		if(combinationTransaction == null){
			combinationTransaction = new CombinationTransaction();
		}
		combinationTransaction.addTransaction(collection);
	}
	
	private ResultSet realSelect(ConnectionSource connectionSource, SQL sql){
		if (debug) {
			Logger.debug("SQL", DataBaseUtils.getSQLId(sql));
		}
		return DataBaseUtils.select(connectionSource, sql);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	ResultSet select(ConnectionSource connectionSource, SQL sql) {
		ResultSet resultSet;
		String id = DataBaseUtils.getSQLId(sql);
		if(cacheMap == null){
			cacheMap = new HashMap<ConnectionSource, Map<String,ResultSet>>(2, 1);
			resultSet = realSelect(connectionSource, sql);
			Map<String, ResultSet> map = new HashMap<String, ResultSet>();
			map.put(id, resultSet);
			cacheMap.put(connectionSource, map);
		}else{
			Map<String, ResultSet> map = cacheMap.getOrDefault(connectionSource, new HashMap<String, ResultSet>());
			if(map == null){
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

	void addSql(ConnectionSource db, Collection<SQL> sqls) {
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
