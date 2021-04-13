package scw.transaction;

/**
 * 事务同步
 * 
 * @author shuchaowen
 *
 */
public interface TransactionSynchronization {

	/**
	 * 提交
	 * 
	 * @throws Throwable
	 */
	void commit() throws Throwable;

	/**
	 * 回滚
	 */
	void rollback();

	/**
	 * commit/rollback 后都会调用此方法,此方法在一个事务中一定会调用，一般用于关闭资源
	 */
	void complete();
}
