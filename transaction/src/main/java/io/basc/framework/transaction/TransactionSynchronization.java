package io.basc.framework.transaction;

/**
 * 事务同步
 * 
 * @author wcnnkh
 *
 */
public interface TransactionSynchronization {
	/**
	 * This method is invoked before the start of the commit process. The method
	 * invocation is done in the context of the transaction that is about to be
	 * committed.
	 */
	void beforeCompletion();

	/**
	 * This method is invoked after the transaction has committed or rolled back.
	 *
	 * @param status The status of the completed transaction.
	 */
	void afterCompletion(TransactionStatus status);
}