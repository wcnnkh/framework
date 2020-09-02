package scw.transaction;

/**
 * 事务的生命周期
 * 
 * @author shuchaowen
 *
 */
public interface TransactionLifeCycle {
	/**
	 * 在commit之前调用(此时事务还未提交，如果发生错误将回滚事务)
	 * @throws Throwable
	 */
	void beforeCommit() throws Throwable;

	/**
	 * 在commit之后调用
	 */
	void afterCommit();

	/**
	 * 在事务回滚前调用
	 * 
	 */
	void beforeRollback();

	/**
	 * 在事务回滚后调用
	 * 
	 */
	void afterRollback();

	/**
	 * 事务结束后调用(一定会被调用)
	 */
	void completion();
}