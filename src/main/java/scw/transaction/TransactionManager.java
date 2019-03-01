package scw.transaction;

public abstract class TransactionManager {
	private TransactionManager() {
	};

	private static final ThreadLocal<Transaction> LOCAL = new ThreadLocal<Transaction>();

	public static Transaction getTransaction(TransactionDefinition transactionDefinition) {
		Transaction context = LOCAL.get();
		if (context != null) {
			context = new Transaction(context);
		}

		switch (transactionDefinition.getPropagation()) {
		case REQUIRED:
			if (context == null) {
				context = new Transaction(transactionDefinition, true);
			}
			break;
		case SUPPORTS:
			if (context == null) {
				context = new Transaction(transactionDefinition, false);
			}
			break;
		case MANDATORY:
			if (context == null || !context.isActive()) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			}
			break;
		case REQUIRES_NEW:
			context = new Transaction(transactionDefinition, true);
			break;
		case NOT_SUPPORTED:
			context = new Transaction(transactionDefinition, false);
			break;
		case NEVER:
			if (context != null && context.isActive()) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			}
			break;
		case NESTED:
			if (context != null && context.isActive()) {
				context.createTempSavePoint();
			} else if (context == null) {
				context = new Transaction(transactionDefinition, true);
			}
			break;
		}
		LOCAL.set(context);
		return context;
	}

	private static void changeLocal(Transaction transaction) {
		if (transaction.getParent() == null) {
			LOCAL.remove();
		} else {
			LOCAL.set(transaction.getParent());
		}
	}

	public static void commit(Transaction transaction) throws TransactionException {
		if (transaction != LOCAL.get()) {
			throw new TransactionException("事务需要顺序执行-commit");
		}

		if (transaction.isComplete()) {
			return;
		}

		transaction.process();
		try {
			transaction.end();
		} finally {
			changeLocal(transaction);
		}
	}

	public static void rollback(Transaction transaction) throws TransactionException {
		if (transaction != LOCAL.get()) {
			throw new TransactionException("事务需要顺序执行-rollback");
		}

		if (transaction.isComplete()) {
			return;
		}

		try {
			transaction.rollback();
		} finally {
			try {
				transaction.end();
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

	public static void transactionLifeCycle(TransactionLifeCycle tlc) {
		Transaction transaction = getCurrentTransaction();
		if (transaction == null) {
			return;
		}

		transaction.transactionLifeCycle(tlc);
	}

	/**
	 * 获取当前运行的事务，如果当前没有事务就返回空
	 * 
	 * @return
	 */
	public static Transaction getCurrentTransaction() {
		return LOCAL.get();
	}
}
