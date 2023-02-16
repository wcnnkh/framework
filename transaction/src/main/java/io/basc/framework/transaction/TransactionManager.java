package io.basc.framework.transaction;

import io.basc.framework.lang.Nullable;

/**
 * 事务管理器
 * 
 * @see ThreadLocalTransactionManager
 * @author shuchaowen
 *
 */
public interface TransactionManager {
	/**
	 * 获取当前事务
	 * 
	 * @return
	 */
	@Nullable
	Transaction getTransaction();

	/**
	 * 当前是否存在事务
	 * 
	 * @return
	 */
	default boolean hasTransaction() {
		return getTransaction() != null;
	}

	/**
	 * 根据规则获取事务
	 * 
	 * @param transactionDefinition
	 * @return
	 * @throws TransactionException
	 */
	default Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
		Transaction transaction = getTransaction();
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
					transaction = new DefaultTransaction(transaction, transactionDefinition, savepoint, true);
				} else {
					transaction = new DefaultTransaction(transaction, transactionDefinition);
				}
			}
			break;
		}
		return transaction;
	}

	/**
	 * 提交一个事务
	 * 
	 * @param transaction
	 * @throws Throwable
	 * @throws TransactionException
	 */
	void commit(Transaction transaction) throws Throwable, TransactionException;

	/**
	 * 回滚一个事务
	 * 
	 * @param transaction
	 * @throws TransactionException
	 */
	void rollback(Transaction transaction) throws TransactionException;
}