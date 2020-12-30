package scw.transaction;

public final class TransactionManager {
	private TransactionManager() {
	};

	private static final ThreadLocal<Transaction> LOCAL = new ThreadLocal<Transaction>();

	/**
	 * 获取事务，会根据事务定义生成指定规则的事务
	 * @param transactionDefinition
	 * @return
	 */
	public static Transaction getTransaction(TransactionDefinition transactionDefinition) {
		Transaction transaction = LOCAL.get();
		switch (transactionDefinition.getPropagation()) {
		case REQUIRED:
			if (transaction == null) {
				transaction = new Transaction(transaction, transactionDefinition, true);
			} else {
				transaction = new Transaction(transaction, transactionDefinition);
			}
			break;
		case SUPPORTS:
			if (transaction == null) {
				transaction = new Transaction(transaction, transactionDefinition, false);
			} else {
				transaction = new Transaction(transaction, transactionDefinition);
			}
			break;
		case MANDATORY:
			if (transaction == null) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			} else {
				if (transaction.isActive()) {
					transaction = new Transaction(transaction, transactionDefinition);
				} else {
					throw new TransactionException(transactionDefinition.getPropagation().name());
				}
			}
			break;
		case REQUIRES_NEW:
			transaction = new Transaction(transaction, transactionDefinition, true);
			break;
		case NOT_SUPPORTED:
			transaction = new Transaction(transaction, transactionDefinition, false);
			break;
		case NEVER:
			if (transaction == null) {
				transaction = new Transaction(transaction, transactionDefinition, false);
			} else {
				if (transaction.isActive()) {
					throw new TransactionException(transactionDefinition.getPropagation().name());
				} else {
					transaction = new Transaction(transaction, transactionDefinition);
				}
			}
			break;
		case NESTED:
			if (transaction == null) {
				transaction = new Transaction(transaction, transactionDefinition, true);
			} else {
				if (transaction.isActive()) {
					transaction = new Transaction(transaction, transactionDefinition);
					transaction.createTempSavepoint();
				} else {
					transaction = new Transaction(transaction, transactionDefinition);
				}
			}
			break;
		}
		LOCAL.set(transaction);
		return transaction;
	}

	private static void changeLocal(Transaction transaction) {
		if (transaction.getParent() == null) {
			LOCAL.remove();
		} else {
			LOCAL.set(transaction.getParent());
		}
	}

	/**
	 * 提交事务
	 * @param transaction
	 * @throws Throwable
	 */
	public static void commit(Transaction transaction) throws Throwable {
		if (transaction.isComplete()) {
			return;
		}

		if (transaction != LOCAL.get()) {
			throw new TransactionException("事务需要顺序执行-commit");
		}

		if (transaction.isRollbackOnly()) {// 直接回滚
			rollback(transaction);
		} else {
			//这里不使用try-finally,所以外部使用出现异常时一定要调用rollback
			transaction.commit();
			try {
				transaction.completion();
			} finally {
				changeLocal(transaction);
			}
		}
	}

	/**
	 * 回滚事务
	 * @param transaction
	 */
	public static void rollback(Transaction transaction) {
		if (transaction.isComplete()) {
			return;
		}

		if (transaction != LOCAL.get()) {
			throw new TransactionException("事务需要顺序执行-rollback");
		}

		try {
			transaction.rollback();
		} finally {
			try {
				transaction.completion();
			} finally {
				changeLocal(transaction);
			}
		}
	}

	/**
	 * 判断当前是否存在事务
	 * 
	 * @return
	 */
	public static boolean hasTransaction() {
		return getCurrentTransaction() != null;
	}

	/**
	 * 加入到当前事务的生命周期
	 * @param tlc
	 */
	public static void addLifecycle(TransactionLifecycle tlc) {
		Transaction transaction = getCurrentTransaction();
		if (transaction == null) {
			return;
		}

		transaction.addLifecycle(tlc);
	}

	/**
	 * 获取当前运行的事务，如果当前没有事务就返回空
	 * 
	 * @return
	 */
	public static Transaction getCurrentTransaction() {
		return LOCAL.get();
	}

	/**
	 * 设置当前事务直接回滚
	 */
	public static void setRollbackOnly() {
		Transaction transaction = getCurrentTransaction();
		if (transaction == null) {
			return;
		}

		transaction.setRollbackOnly(true);
	}
}
