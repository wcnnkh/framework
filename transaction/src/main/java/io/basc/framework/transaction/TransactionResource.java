package io.basc.framework.transaction;

/**
 * 事务资源
 * 
 * @author shuchaowen
 *
 */
public interface TransactionResource extends TransactionSynchronization {

	/**
	 * 创建一个保存点
	 * 
	 * @return
	 * @throws TransactionException
	 */
	Savepoint createSavepoint() throws TransactionException;
}
