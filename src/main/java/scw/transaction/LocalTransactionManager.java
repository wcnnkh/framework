package scw.transaction;

/**
 * 事务管理器
 * 
 * @author shuchaowen
 *
 */
public abstract class LocalTransactionManager {
	private final static ThreadLocal<TransactionContext> context = new ThreadLocal<TransactionContext>();
	/**
	 * 全局配置
	 */
	private final static TransactionConfig GLOBA_CONFIG = new TransactionConfig(true, false);// 默认开启事务

	/**
	 * 全局事务配置
	 * 
	 * @return
	 */
	public static TransactionConfig getGlobaConfig() {
		return GLOBA_CONFIG;
	}

	public static void before() {
		TransactionContext transactionContext = context.get();
		if (transactionContext == null) {
			transactionContext = new TransactionContext(GLOBA_CONFIG);
			context.set(transactionContext);
		}
		transactionContext.before();
	}

	public static void after() {
		TransactionContext transactionContext = context.get();
		if (transactionContext == null) {
			throw new IllegalStateException("method context not before");
		}
		transactionContext.after();
	}

	public static void complete() {
		TransactionContext transactionContext = context.get();
		if (transactionContext == null) {
			throw new IllegalStateException("method context not before");
		}

		try {
			transactionContext.complete();
		} finally {
			if (transactionContext.isLastComplete()) {
				context.remove();
			}
		}
	}

	public static TransactionResource getResource() {
		TransactionContext transactionContext = context.get();
		if (transactionContext == null) {
			throw new IllegalStateException("method context not before");
		}
		return transactionContext.getResource();
	}
}
