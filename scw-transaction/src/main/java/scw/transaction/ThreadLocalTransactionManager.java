package scw.transaction;

import scw.lang.NamedThreadLocal;


public class ThreadLocalTransactionManager implements TransactionManager{
	private final ThreadLocal<DefaultTransaction> local = new NamedThreadLocal<DefaultTransaction>(ThreadLocalTransactionManager.class.getSimpleName());

	/**
	 * 获取事务，会根据事务定义生成指定规则的事务
	 * @param transactionDefinition
	 * @return
	 */
	public DefaultTransaction getTransaction(TransactionDefinition transactionDefinition) {
		DefaultTransaction transaction = local.get();
		switch (transactionDefinition.getPropagation()) {
		case REQUIRED:
			if (transaction == null) {
				transaction = new DefaultTransaction(transaction, transactionDefinition, true);
			} else {
				transaction = new DefaultTransaction(transaction, transactionDefinition);
			}
			break;
		case SUPPORTS:
			if (transaction == null) {
				transaction = new DefaultTransaction(transaction, transactionDefinition, false);
			} else {
				transaction = new DefaultTransaction(transaction, transactionDefinition);
			}
			break;
		case MANDATORY:
			if (transaction == null) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			} else {
				if (transaction.isActive()) {
					transaction = new DefaultTransaction(transaction, transactionDefinition);
				} else {
					throw new TransactionException(transactionDefinition.getPropagation().name());
				}
			}
			break;
		case REQUIRES_NEW:
			transaction = new DefaultTransaction(transaction, transactionDefinition, true);
			break;
		case NOT_SUPPORTED:
			transaction = new DefaultTransaction(transaction, transactionDefinition, false);
			break;
		case NEVER:
			if (transaction == null) {
				transaction = new DefaultTransaction(transaction, transactionDefinition, false);
			} else {
				if (transaction.isActive()) {
					throw new TransactionException(transactionDefinition.getPropagation().name());
				} else {
					transaction = new DefaultTransaction(transaction, transactionDefinition);
				}
			}
			break;
		case NESTED:
			if (transaction == null) {
				transaction = new DefaultTransaction(transaction, transactionDefinition, true);
			} else {
				if (transaction.isActive()) {
					Savepoint savepoint = transaction.createSavepoint();
					transaction = new DefaultTransaction(transaction, transactionDefinition, savepoint);
				} else {
					transaction = new DefaultTransaction(transaction, transactionDefinition);
				}
			}
			break;
		}
		local.set(transaction);
		return transaction;
	}

	private void changeLocal(DefaultTransaction transaction) {
		if (transaction.getParent() == null) {
			local.remove();
		} else {
			local.set(transaction.getParent());
		}
	}

	/**
	 * 提交事务
	 * @param transaction
	 * @throws Throwable
	 */
	public void commit(Transaction transaction) throws Throwable {
		if (transaction.isCompleted()) {
			return;
		}

		DefaultTransaction localTransaction = local.get();
		if (transaction != localTransaction) {
			throw new TransactionException("事务需要顺序执行-commit");
		}

		if (localTransaction.isRollbackOnly()) {// 直接回滚
			rollback(transaction);
		} else {
			//这里不使用try-finally,所以外部使用出现异常时一定要调用rollback
			localTransaction.commit();
			try {
				localTransaction.complete();
			} finally {
				changeLocal(localTransaction);
			}
		}
	}

	/**
	 * 回滚事务
	 * @param transaction
	 */
	public void rollback(Transaction transaction) {
		if (transaction.isCompleted()) {
			return;
		}

		DefaultTransaction localTransaction = local.get();
		if (transaction != localTransaction) {
			throw new TransactionException("事务需要顺序执行-rollback");
		}

		try {
			localTransaction.rollback();
		} finally {
			try {
				localTransaction.complete();
			} finally {
				changeLocal(localTransaction);
			}
		}
	}

	public boolean hasTransaction() {
		return getTransaction() != null;
	}

	public DefaultTransaction getTransaction() {
		return local.get();
	}
}
