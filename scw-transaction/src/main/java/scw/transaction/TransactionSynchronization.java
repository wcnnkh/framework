package scw.transaction;

public interface TransactionSynchronization {

	void commit() throws Throwable;

	void rollback();

	/**
	 * commit/rollback 后都会调用此方法,此方法在一个事务中一定会调用，一般用于关闭资源
	 */
	void complete();
}
