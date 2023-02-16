package io.basc.framework.transaction;

import io.basc.framework.env.Sys;
import io.basc.framework.lang.NamedThreadLocal;

public final class TransactionUtils {

	private TransactionUtils() {
	};

	/**
	 * Global default transactions(全局默认使用的事务)
	 */
	private static final TransactionManager DEFAULT = Sys.getEnv()
			.getServiceLoader(TransactionManager.class, ThreadLocalTransactionManager.class).first();
	private static final ThreadLocal<TransactionManager> LOCAL = new NamedThreadLocal<TransactionManager>(
			TransactionManager.class.getSimpleName());

	/**
	 * 获取默认的管理器
	 * 
	 * @return
	 */
	public static TransactionManager getDefaultManager() {
		return DEFAULT;
	}

	/**
	 * 获取当前使用的事务管理器
	 * 
	 * @return
	 */
	public static TransactionManager getManager() {
		TransactionManager manager = LOCAL.get();
		return manager == null ? DEFAULT : manager;
	}

	/**
	 * 当前是否存在事务管理器
	 * 
	 * @return
	 */
	public static boolean hasManager() {
		return LOCAL.get() != null;
	}

	/**
	 * 设置事务管理器
	 * 
	 * @param manager
	 * @return 返回旧的事务管理器
	 */
	public static TransactionManager setManager(TransactionManager manager) {
		TransactionManager old = LOCAL.get();
		if (manager == null) {
			LOCAL.remove();
		} else {
			LOCAL.set(manager);
		}
		return old;
	}

	/**
	 * 获取当前事务
	 * 
	 * @return
	 */
	public static Transaction getTransaction() {
		TransactionManager manager = getManager();
		Transaction transaction = manager.getTransaction();
		return transaction == null ? null : new ManagerTransaction(manager, transaction);
	}

	/**
	 * 根据规则获取事务
	 * 
	 * @param definition
	 * @return
	 */
	public static Transaction getTransaction(TransactionDefinition definition) {
		TransactionManager manager = getManager();
		Transaction transaction = manager.getTransaction(definition);
		return transaction == null ? null : new ManagerTransaction(manager, transaction);
	}
}
