package scw.database;

import java.util.Arrays;
import java.util.Collection;

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
	private volatile boolean selectCache = true;// 是否开启查询缓存 默认开启

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

		ThreadLocalTransaction threadLocalTransactionInfo = getValue();
		if (threadLocalTransactionInfo == null) {// 如果未使用事务
			AbstractTransaction.transaction(transaction);
		} else {
			threadLocalTransactionInfo.addTransaction(transaction);
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
		ThreadLocalTransaction threadLocalTransactionInfo = getValue();
		if (threadLocalTransactionInfo == null) {// 如果未使用事务
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
			if (threadLocalTransactionInfo.isDebug()) {
				if (sqls != null) {
					for (SQL s : sqls) {
						Logger.debug("SQL", DataBaseUtils.getSQLId(s));
					}
				}
			}

			threadLocalTransactionInfo.addSql(connectionSource, sqls);
			if (transaction != null) {
				threadLocalTransactionInfo.addTransaction(transaction);
			}
		}
	}

	/**
	 * @param connectionSource
	 * @param sql
	 * @return ResultSet不可能为空
	 */
	public ResultSet select(ConnectionSource connectionSource, SQL sql, boolean isTransaction) {
		if (isTransaction) {
			ThreadLocalTransaction threadLocalTransactionInfo = getValue();
			if (threadLocalTransactionInfo != null) {// 如果使用事务
				return threadLocalTransactionInfo.select(connectionSource, sql);
			}
		}

		if (debug) {
			Logger.debug("SQL", DataBaseUtils.getSQLId(sql));
		}
		return DataBaseUtils.select(connectionSource, sql);
	}

	/**
	 * 消除当前上下文的查询缓存
	 */
	public void clearSelectCache() {
		ThreadLocalTransaction threadLocalTransactionInfo = getValue();
		boolean debug;
		if (threadLocalTransactionInfo == null) {
			debug = true;
		} else {
			debug = threadLocalTransactionInfo.isDebug();
		}

		if (debug) {
			Logger.debug(this.getClass().getName(), "clear select cache");
		}

		if (threadLocalTransactionInfo != null) {
			threadLocalTransactionInfo.clearSelectCache();
		}
	}

	public void setSelectCache(boolean selectCache) {
		ThreadLocalTransaction threadLocalTransactionInfo = getValue();
		if (threadLocalTransactionInfo != null) {
			threadLocalTransactionInfo.setSelectCache(selectCache);
		}
	}

	public boolean isDebug() {
		ThreadLocalTransaction threadLocalTransactionInfo = getValue();
		return threadLocalTransactionInfo == null ? debug : threadLocalTransactionInfo.isDebug();
	}

	public void setDebug(boolean debug) {
		ThreadLocalTransaction threadLocalTransactionInfo = getValue();
		if (threadLocalTransactionInfo != null) {
			threadLocalTransactionInfo.setDebug(debug);
		}
	}

	/**
	 * 设置全局的DEBUG状态
	 * 
	 * @param debug
	 */
	public void setGlobalDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * 设置全局的查询缓存是否开启
	 * 
	 * @param selectCache
	 */
	public void setGlobalSelectCache(boolean selectCache) {
		this.selectCache = selectCache;
	}

	public boolean isSelectCache() {
		ThreadLocalTransaction threadLocalTransactionInfo = getValue();
		return threadLocalTransactionInfo == null ? selectCache : threadLocalTransactionInfo.isSelectCache();
	}

	@Override
	public void begin() {
		super.begin();
	}

	@Override
	protected void lastCommit() {
		ThreadLocalTransaction transaction = getValue();
		if (transaction != null) {
			transaction.execute();
		}
	}

	@Override
	protected void firstBegin() {
		setValue(new ThreadLocalTransaction(debug, selectCache));
	}
}
