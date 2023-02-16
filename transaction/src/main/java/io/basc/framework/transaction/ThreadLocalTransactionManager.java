package io.basc.framework.transaction;

import io.basc.framework.lang.NamedThreadLocal;

public class ThreadLocalTransactionManager implements TransactionManager {
	private final ThreadLocal<Transaction> local = new NamedThreadLocal<Transaction>(Transaction.class.getSimpleName());

	@Override
	public Transaction getTransaction(TransactionDefinition transactionDefinition) {
		Transaction transaction = TransactionManager.super.getTransaction(transactionDefinition);
		local.set(transaction);
		return transaction;
	}

	private Transaction cleanup(Transaction transaction) {
		if (transaction == null || !transaction.isCompleted()) {
			return transaction;
		}

		Transaction parent = transaction.getParent();
		while (parent != null && parent.isCompleted()) {
			parent = parent.getParent();
		}

		if (parent == null) {
			local.remove();
		} else {
			local.set(parent);
		}
		return parent;
	}

	/**
	 * 提交事务
	 * 
	 * @param transaction
	 * @throws Throwable
	 */
	public void commit(Transaction transaction) throws Throwable {
		if (transaction.isCompleted()) {
			return;
		}

		Transaction localTransaction = local.get();
		if (transaction != localTransaction) {
			throw new TransactionException("事务需要顺序执行-commit");
		}

		if (localTransaction.isRollbackOnly()) {// 直接回滚
			rollback(transaction);
		} else {
			// 这里不使用try-finally,所以外部使用出现异常时一定要调用rollback
			try {
				localTransaction.commit();
			} finally {
				cleanup(localTransaction);
			}
		}
	}

	/**
	 * 回滚事务
	 * 
	 * @param transaction
	 */
	public void rollback(Transaction transaction) {
		if (transaction.isCompleted()) {
			return;
		}

		Transaction localTransaction = local.get();
		if (transaction != localTransaction) {
			throw new TransactionException("事务需要顺序执行-rollback");
		}

		try {
			localTransaction.rollback();
		} finally {
			cleanup(localTransaction);
		}
	}

	public Transaction getTransaction() {
		return cleanup(local.get());
	}
}
