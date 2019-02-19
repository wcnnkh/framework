package scw.database;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import scw.common.Logger;
import scw.common.transaction.AbstractTransaction;
import scw.common.transaction.Transaction;
import scw.common.utils.Assert;
import scw.sql.Sql;
import scw.sql.SqlUtils;
import scw.sql.orm.result.ResultSet;

/**
 * 数据库封装核心类，用于处理数据库事务
 * 
 * @author shuchaowen
 */
public final class TransactionContext {
	private static final ThreadLocal<TransactionContextInfo> CONTEXT = new ThreadLocal<TransactionContextInfo>();
	private static volatile ContextConfig GLOBA_CONFIG = new ContextConfig(false, true, false);

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
		return CONTEXT.get().getTransactionContextQuarantine().getConfig();
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
		TransactionContextInfo info = CONTEXT.get();
		if (info.getIndex() == 1) {
			try {
				info.end();
			} finally {
				CONTEXT.remove();
			}
		} else {
			info.end();
		}
	}

	public static void execute(Transaction transaction) {
		TransactionContextInfo contextInfo = CONTEXT.get();
		if (contextInfo == null) {
			AbstractTransaction.transaction(transaction);
		} else {
			TransactionContextQuarantine quarantine = contextInfo.getTransactionContextQuarantine();
			if (quarantine.getConfig().isAutoCommit()) {
				AbstractTransaction.transaction(transaction);
			} else {
				quarantine.addTransaction(transaction);
			}
		}
	}

	private static void debug(Collection<Sql> sqls) {
		if (sqls != null) {
			Iterator<Sql> iterator = sqls.iterator();
			while (iterator.hasNext()) {
				Sql sql = iterator.next();
				debug(sql);
			}
		}
	}

	private static void debug(Sql sql) {
		if (sql == null) {
			return;
		}

		Logger.debug(TransactionContext.class.getName(), SqlUtils.getSqlId(sql));
	}

	private static void forceExecute(ConnectionSource connectionSource, Collection<Sql> sqls, boolean debug) {
		SQLTransaction sqlTransaction = new SQLTransaction(connectionSource);
		Iterator<Sql> iterator = sqls.iterator();
		while (iterator.hasNext()) {
			sqlTransaction.addSql(iterator.next());
		}

		if (debug) {
			debug(sqls);
		}

		AbstractTransaction.transaction(sqlTransaction);
	}

	public static void execute(ConnectionSource connectionSource, Collection<Sql> sqls) {
		Assert.notNull(connectionSource);
		Assert.notNull(sqls);

		TransactionContextInfo contextInfo = CONTEXT.get();
		if (contextInfo == null) {
			forceExecute(connectionSource, sqls, GLOBA_CONFIG.isDebug());
		} else {
			TransactionContextQuarantine quarantine = contextInfo.getTransactionContextQuarantine();
			if (quarantine.getConfig().isDebug()) {
				debug(sqls);
			}

			if (quarantine.getConfig().isAutoCommit()) {
				forceExecute(connectionSource, sqls, false);
			} else {
				quarantine.addSql(connectionSource, sqls);
			}
		}
	}

	public static void execute(ConnectionSource connectionSource, Sql... sqls) {
		execute(connectionSource, Arrays.asList(sqls));
	}

	/**
	 * @param connectionSource
	 * @param sql
	 * @return ResultSet不可能为空
	 */
	public static ResultSet select(ConnectionSource connectionSource, Sql sql) {
		Assert.notNull(connectionSource);
		Assert.notNull(sql);

		TransactionContextInfo contextInfo = CONTEXT.get();
		if (contextInfo == null) {
			if (GLOBA_CONFIG.isDebug()) {
				debug(sql);
			}
			return DataBaseUtils.select(connectionSource, sql);
		} else {
			TransactionContextQuarantine quarantine = contextInfo.getTransactionContextQuarantine();
			if (quarantine.getConfig().isSelectCache()) {
				return contextInfo.select(connectionSource, sql);
			} else {
				if (quarantine.getConfig().isDebug()) {
					debug(sql);
				}
				return DataBaseUtils.select(connectionSource, sql);
			}
		}
	}

	/**
	 * 添加对事务生命周期的监听
	 * 
	 * @param lifeCycle
	 */
	public static void addTransactionLifeCycleListenin(TransactionLifeCycle lifeCycle) {
		if (lifeCycle == null) {
			return;
		}

		TransactionContextInfo contextInfo = CONTEXT.get();
		if (contextInfo == null) {
			return;
		}

		TransactionContextQuarantine quarantine = contextInfo.getTransactionContextQuarantine();
		quarantine.addTransactionLifeCycleListenin(lifeCycle);
	}
}
