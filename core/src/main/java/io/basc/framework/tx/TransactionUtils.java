package io.basc.framework.tx;

import io.basc.framework.env.Sys;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

public final class TransactionUtils {

	private TransactionUtils() {
	};

	/**
	 * Global default transactions(全局默认使用的事务)
	 */
	private static final TransactionManager DEFAULT = Sys.getEnv()
			.getServiceLoader(TransactionManager.class, ThreadLocalTransactionManager.class).getServices().first();
	private static final ThreadLocal<TransactionManager> LOCAL = new NamedThreadLocal<TransactionManager>(
			TransactionManager.class.getSimpleName());

	public static TransactionManager getDefaultManager() {
		return DEFAULT;
	}

	public static TransactionManager getManager() {
		TransactionManager manager = LOCAL.get();
		return manager == null ? DEFAULT : manager;
	}

	public static void setManager(TransactionManager manager) {
		if (manager == null) {
			LOCAL.remove();
		} else {
			LOCAL.set(manager);
		}
	}

	@Nullable
	public static FacadeTransaction getTransaction() {
		return getTransaction(null);
	}

	@Nullable
	public static FacadeTransaction getTransaction(TransactionDefinition definition) {
		return getTransaction(getManager(), definition);
	}

	@Nullable
	public static FacadeTransaction getTransaction(TransactionManager manager,
			@Nullable TransactionDefinition definition) {
		Assert.requiredArgument(manager != null, "manager");
		Transaction transaction = definition == null ? manager.getTransaction() : manager.getTransaction(definition);
		if (transaction == null) {
			return null;
		}

		if (transaction instanceof FacadeTransaction) {
			return (FacadeTransaction) transaction;
		}

		return new FacadeTransaction(manager, transaction);
	}
}
