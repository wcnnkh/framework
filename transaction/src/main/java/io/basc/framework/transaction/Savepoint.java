package io.basc.framework.transaction;

/**
 * 保存点
 * 
 * @author shuchaowen
 *
 */
public interface Savepoint {
	public static Savepoint EMPTY = new EmptySavepoint();
	
	void rollback() throws TransactionException;

	void release() throws TransactionException;
}
