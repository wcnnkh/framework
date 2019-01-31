package scw.transaction;

/**
 * 同步事务
 * 
 * @author shuchaowen
 *
 */
public interface TransactionSynchronization {
	void beforeCommit() throws Throwable;

	void commit() throws Throwable;

	void afterCommit() throws Throwable;

	void rollback();

	void complete();
}
