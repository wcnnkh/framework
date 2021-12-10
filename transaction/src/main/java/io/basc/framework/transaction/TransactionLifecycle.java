package io.basc.framework.transaction;

/**
 * 事务的生命周期
 * 
 * @author shuchaowen
 *
 */
public interface TransactionLifecycle {
	/**
	 * 在commit之前调用(此时事务还未提交，如果发生错误将回滚事务)
	 * 
	 * @throws Throwable
	 */
	void beforeCommit() throws Throwable;

	/**
	 * 在commit之后调用(此时事务已提交，如果发生错误是无法回滚的)
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
	 * 事务结束后调用
	 */
	void complete();
}
