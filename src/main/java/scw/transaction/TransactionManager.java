package scw.transaction;

import scw.beans.annotaion.DisableProxy;

@DisableProxy
public interface TransactionManager {
	
	Transaction getTransaction(TransactionDefinition transactionDefinition)
			throws TransactionException;

	void commit(Transaction transaction) throws TransactionException;

	void rollback(Transaction transaction) throws TransactionException;
	
	/**
	 * 当前上下文是否存在事务
	 * @return
	 */
	boolean hasTransaction();
}
