package scw.transaction;

import scw.instance.InstanceUtils;
import scw.lang.NamedThreadLocal;

public final class TransactionUtils {

	private TransactionUtils() {
	};

	/**
	 * Global default transactions(全局默认使用的事务)
	 */
	private static final TransactionManager DEFAULT = InstanceUtils
			.loadService(TransactionManager.class,
					"scw.transaction.ThreadLocalTransactionManager");
	private static ThreadLocal<TransactionManager> LOCAL = new NamedThreadLocal<TransactionManager>(
			TransactionUtils.class.getSimpleName());

	/**
	 * 获取默认的管理器
	 * @return
	 */
	public static TransactionManager getDefaultManager() {
		return DEFAULT;
	}

	/**
	 * 获取当前使用的事务管理器
	 * @return
	 */
	public static TransactionManager getManager() {
		TransactionManager manager = LOCAL.get();
		return manager == null ? DEFAULT : manager;
	}
	
	public static boolean hasManager(){
		return LOCAL.get() != null;
	}

	/**
	 * 设置事务管理器
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
}
