package scw.database;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import scw.common.Logger;
import scw.common.transaction.AbstractTransaction;
import scw.common.transaction.Transaction;
import scw.common.utils.Assert;
import scw.database.result.ResultSet;

/**
 * 数据库封装核心类，用于处理数据库事务
 * 
 * @author shuchaowen
 */
public final class TransactionContext {
	private static final ThreadLocal<TransactionContextInfo> CONTEXT = new ThreadLocal<TransactionContextInfo>();
	private static volatile ContextConfig GLOBA_CONFIG = new ContextConfig(
			false, true, false);

	/**
	 * 获取全局配置
	 * 
	 * @return
	 */
	public static ContextConfig getGlobaConfig() {
		return GLOBA_CONFIG;
	}

	/**
	 * 此方法要在调用begin之后才可以调用 begin try{ commit }finally{ end }
	 * 
	 * @return
	 */
	public static ContextConfig getConfig() {
		return CONTEXT.get().getTransactionContextConfig();
	}

	/**
	 * begin try{ commit }finally{ end }
	 */
	public static void begin() {
		TransactionContextInfo contextInfo = CONTEXT.get();
		if (contextInfo == null) {// 第一次开始
			contextInfo = new TransactionContextInfo(GLOBA_CONFIG);
			CONTEXT.set(contextInfo);
		}
		contextInfo.begin();
	}

	/**
	 * 此方法要在调用begin之后才可以调用 begin try{ commit }finally{ end }
	 * 
	 * @return
	 */
	public static void commit() {
		CONTEXT.get().commit();
	}

	/**
	 * 此方法要在调用begin之后才可以调用 begin try{ commit }finally{ end }
	 * 
	 * @return
	 */
	public static void end() {
		CONTEXT.get().end();
	}

	public static void execute(Transaction transaction) {
		TransactionContextInfo contextInfo = CONTEXT.get();
		if (contextInfo == null) {
			AbstractTransaction.transaction(transaction);
		} else {
			if (contextInfo.getTransactionContextConfig().isAutoCommit()) {
				AbstractTransaction.transaction(transaction);
			} else {
				contextInfo.getTransactionContextQuarantine().addTransaction(
						transaction);
			}
		}
	}

	private static void debug(Collection<SQL> sqls) {
		Assert.isNull(sqls);

		Iterator<SQL> iterator = sqls.iterator();
		while (iterator.hasNext()) {
			SQL sql = iterator.next();
			if (sql == null) {
				continue;
			}

			Logger.debug(TransactionContext.class.getName(),
					DataBaseUtils.getSQLId(sql));
		}
	}

	private static void forceExecute(ConnectionSource connectionSource,
			Collection<SQL> sqls, boolean debug) {
		SQLTransaction sqlTransaction = new SQLTransaction(connectionSource);
		Iterator<SQL> iterator = sqls.iterator();
		while (iterator.hasNext()) {
			sqlTransaction.addSql(iterator.next());
		}

		if (debug) {
			debug(sqls);
		}

		AbstractTransaction.transaction(sqlTransaction);
	}

	public static void execute(ConnectionSource connectionSource,
			Collection<SQL> sqls) {
		Assert.isNull(connectionSource);
		Assert.isNull(sqls);

		TransactionContextInfo contextInfo = CONTEXT.get();
		if (contextInfo == null) {
			forceExecute(connectionSource, sqls, GLOBA_CONFIG.isDebug());
		} else {
			if (contextInfo.getTransactionContextConfig().isAutoCommit()) {
				forceExecute(connectionSource, sqls, contextInfo
						.getTransactionContextConfig().isDebug());
			} else {
				contextInfo.getTransactionContextQuarantine().addSql(
						connectionSource, sqls);
			}
		}
	}

	public static void execute(ConnectionSource connectionSource, SQL... sqls) {
		execute(connectionSource, Arrays.asList(sqls));
	}

	/**
	 * @param connectionSource
	 * @param sql
	 * @return ResultSet不可能为空
	 */
	public static ResultSet select(ConnectionSource connectionSource, SQL sql) {
		Assert.isNull(connectionSource);
		Assert.isNull(sql);

		TransactionContextInfo contextInfo = CONTEXT.get();
		if (contextInfo == null) {
			if (GLOBA_CONFIG.isDebug()) {
				debug(Arrays.asList(sql));
			}
			return DataBaseUtils.select(connectionSource, sql);
		} else {
			if (contextInfo.getTransactionContextConfig().isSelectCache()) {
				return contextInfo.select(connectionSource, sql);
			} else {
				if (contextInfo.getTransactionContextConfig().isDebug()) {
					debug(Arrays.asList(sql));
				}
				return DataBaseUtils.select(connectionSource, sql);
			}
		}
	}
}
