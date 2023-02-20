package io.basc.framework.transaction;

public interface SavepointManager {
	/**
	 * 创建一个保存点
	 * 
	 * @return
	 * @throws TransactionException
	 */
	Savepoint createSavepoint() throws TransactionException;
}
