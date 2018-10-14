package shuchaowen.core.db;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.transaction.AbstractTransaction;
import shuchaowen.core.db.transaction.SQLTransaction;
import shuchaowen.core.db.transaction.Transaction;
import shuchaowen.core.db.transaction.TransactionCollection;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.Logger;

/**
 * 数据库封装核心类，用于处理数据库事务
 * 
 * @author shuchaowen
 *
 */
public final class TransactionContext {
	private ThreadLocal<ThreadLocalDBTransaction> threadLocalTransaction = new ThreadLocal<ThreadLocalDBTransaction>();
	private volatile boolean cacheEnable;
	// 默认应该是开启事务的，不然要这个类有何用，定义此字段是为了满足特殊需求
	private volatile boolean transactionEnable = true;
	private volatile boolean sqlDebug = false;

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
		this(true);
	}

	/**
	 * @param cacheEnable
	 *            是否启用同一事务内的查询缓存
	 */
	public TransactionContext(boolean cacheEnable) {
		this.cacheEnable = cacheEnable;
	}

	public boolean isTransactionEnable() {
		return transactionEnable;
	}

	public void setTransactionEnable(boolean transactionEnable) {
		this.transactionEnable = transactionEnable;
	}
	
	private static void logger(SQL sql){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(sql.getSql());
		sb.append("]");
		sb.append(" - ");
		sb.append(sql.getParams() == null? "[]":Arrays.toString(sql.getParams()));
		Logger.debug("SQL", sb.toString());
	}
	
	/**
	 * 不参与正在进行的事务，单独执行
	 */
	public void forceExecute(AbstractDB db, Collection<SQL> sqls){
		if (sqls == null || db == null) {
			throw new NullPointerException();
		}
		
		if (sqlDebug) {
			for (SQL s : sqls) {
				logger(s);
			}
		}
		
		DBUtils.execute(db, sqls);
	}

	/**
	 * 开始一个事务，如果已经开始了事务会将事务计数器加一
	 */
	public void begin() {
		if (!transactionEnable) {
			return;
		}

		ThreadLocalDBTransaction sqlTransaction = threadLocalTransaction.get();
		if (sqlTransaction == null) {
			sqlTransaction = new ThreadLocalDBTransaction();
			threadLocalTransaction.set(sqlTransaction);
		}
		sqlTransaction.beginTransaction();
	}

	/**
	 * 开始的事务必须要提交
	 * begin与commit是一一对应的,开始了多少次就要提交多少次
	 * 当事务的索引为1说明这是最后一个事务了，此时会提交事务
	 * @throws Throwable
	 *             如果出现异常说明事务执行失败
	 */
	public void commit() throws Exception {
		ThreadLocalDBTransaction sqlTransaction = threadLocalTransaction.get();
		if (sqlTransaction == null) {
			throw new NullPointerException("Please start the transaction first");
		}
		sqlTransaction.commitTransaction();
	}

	/**
	 * 此方式还不成熟，在要求不太高的情况下可以试试
	 * 
	 * @param transaction
	 */
	public void execute(Collection<Transaction> transactions) {
		if (transactions == null || transactions.size() == 0) {
			return;
		}

		ThreadLocalDBTransaction localDBTransaction = threadLocalTransaction.get();
		if (!transactionEnable || localDBTransaction == null || localDBTransaction.isAutoCommit()) {// 如果未使用事务
			TransactionCollection transactionCollection = new TransactionCollection(transactions);
			try {
				transactionCollection.execute();
			} catch (Throwable e) {
				throw new ShuChaoWenRuntimeException(e);
			}
		} else {
			localDBTransaction.addTransaction(transactions);
		}
	}
	
	public void execute(AbstractDB db, SQL ...sqls){
		execute(db, Arrays.asList(sqls));
	}

	/**
	 * 提交一个SQL语句，如果已经开启了事务会使用事务的方式，如果未开启事务就会直接执行
	 * 
	 * @param connectionOrigin
	 * @param sql
	 */
	public void execute(AbstractDB db, Collection<SQL> sqls) {
		if (db == null || sqls == null || sqls.isEmpty()) {
			return;
		}

		if (sqlDebug) {
			for (SQL s : sqls) {
				logger(s);
			}
		}

		ThreadLocalDBTransaction localDBTransaction = threadLocalTransaction.get();
		if (!transactionEnable || localDBTransaction == null || localDBTransaction.isAutoCommit()) {// 如果未使用事务
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

	public ResultSet select(AbstractDB db, SQL sql){
		if(sqlDebug){
			logger(sql);
		}
		
		if (cacheEnable) {
			ThreadLocalDBTransaction localDBTransaction = threadLocalTransaction.get();
			if (localDBTransaction == null || localDBTransaction.isAutoCommit()) {// 如果未使用事务
				return DBUtils.select(db, sql);
			} else {
				return localDBTransaction.select(db, sql);
			}
		} else {
			return DBUtils.select(db, sql);
		}
	}
	
 	/**
	 * 判断事务是否开启
	 * 
	 * @return
	 */
	public boolean isBegin() {
		ThreadLocalDBTransaction localDBTransaction = threadLocalTransaction.get();
		return localDBTransaction != null && !threadLocalTransaction.get().isAutoCommit();
	}

	/**
	 * 事务开始了多少次
	 * 
	 * @return
	 */
	public int getBeginCount() {
		ThreadLocalDBTransaction localDBTransaction = threadLocalTransaction.get();
		return localDBTransaction == null ? 0 : localDBTransaction.getTransactionCount();
	}

	public boolean isSqlDebug() {
		return sqlDebug;
	}

	public void setSqlDebug(boolean sqlDebug) {
		this.sqlDebug = sqlDebug;
	}
}

class ThreadLocalDBTransaction extends AbstractTransaction {
	private HashMap<AbstractDB, SQLTransaction> dbSqlMap = new HashMap<AbstractDB, SQLTransaction>();
	private Map<AbstractDB, Map<String, ResultSet>> cacheMap = new HashMap<AbstractDB, Map<String, ResultSet>>();
	private TransactionCollection transactionCollection = new TransactionCollection();

	/**
	 * 事务开始次数 用来实现事务的合并 意思就是说事务开始了多少次就要提交多少次,只有最后一次提交才是真实提交
	 */
	private int transactionCount = 0;

	private boolean isAutoCommit = true;// 是否是自动提交

	public boolean isAutoCommit() {
		return isAutoCommit;
	}

	public int getTransactionCount() {
		return transactionCount;
	}

	void addTransaction(Collection<Transaction> collection) {
		transactionCollection.add(new TransactionCollection(collection));
	}

	void beginTransaction() {
		if (isAutoCommit) {// 如果原来是自动提交，现在改为手动提交，为了防止脏数据应该先清除一遍
			reset();
			isAutoCommit = false;
		}
		transactionCount++;
	}

	void commitTransaction() throws Exception{
		if (isAutoCommit) {
			throw new ShuChaoWenRuntimeException("transaction status error autoCommit[" + isAutoCommit + "]");
		}

		if (transactionCount <= 0) {
			throw new IndexOutOfBoundsException("transactionCount=" + transactionCount);
		}

		transactionCount --;
		if (transactionCount == 0) {
			//应该要提交事务了了
			try {
				execute();
			} catch (Exception e) {
				throw e;
			}finally{
				isAutoCommit = true;
				reset();
			}
		}
	}

	ResultSet select(AbstractDB db, SQL sql) {
		if (isAutoCommit) {
			return DBUtils.select(db, sql);
		} else {
			ResultSet resultSet = null;
			String id =  DBUtils.getSQLId(sql);
			Map<String, ResultSet> map = cacheMap.getOrDefault(id, new HashMap<String, ResultSet>());
			if(map.containsKey(id)){
				return map.get(id);
			}else{
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
		for (Entry<AbstractDB, SQLTransaction> entry : dbSqlMap.entrySet()) {
			entry.getValue().clear();
		}
		transactionCount = 0;
	}

	void addSql(AbstractDB db, Collection<SQL> sqls) {
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
		for (Entry<AbstractDB, SQLTransaction> entry : dbSqlMap.entrySet()) {
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
