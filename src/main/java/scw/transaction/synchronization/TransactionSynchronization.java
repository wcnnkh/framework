package scw.transaction.synchronization;

import scw.transaction.TransactionException;

/**
 * 同步事务
 * 
 * @author shuchaowen
 *
 */
public interface TransactionSynchronization {

	void beforeCommit() throws TransactionException;

	void afterCommit() throws TransactionException;

	void rollback() throws TransactionException;
	
	void complete() throws TransactionException;
}
