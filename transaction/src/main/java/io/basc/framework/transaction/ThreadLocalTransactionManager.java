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
		Transaction tx = transaction;
		while (tx != null && tx.getStatus().isCompleted()) {
			tx = tx.getParent();
		}

		if (tx == null) {
			if (transaction != null) {
				local.remove();
			}
		} else if (!tx.equals(transaction)) {
			local.set(tx);
		}
		return tx;
	}

	public void commit(Transaction transaction) throws Throwable {
		if (transaction.isRollbackOnly()) {// 直接回滚
			rollback(transaction);
			return;
		}

		if (transaction.getStatus().isCommitting()) {
			return;
		}

		// 这里不使用try-finally,所以外部使用出现异常时一定要调用rollback
		transaction.commit();
		try {
			transaction.close();
		} finally {
			cleanup(transaction);
		}
	}

	public void rollback(Transaction transaction) {
		if (transaction.getStatus().isRolledBack()) {
			return;
		}

		try {
			if (!transaction.getStatus().isCommitted() && !transaction.getStatus().isCompleted()) {
				transaction.rollback();
			}
		} finally {
			try {
				transaction.close();
			} finally {
				cleanup(transaction);
			}
		}
	}

	public Transaction getTransaction() {
		return cleanup(local.get());
	}
}
