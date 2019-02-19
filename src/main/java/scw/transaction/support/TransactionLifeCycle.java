package scw.transaction.support;

/**
 * 事务的生命周期
 * 
 * @author shuchaowen
 *
 */
public interface TransactionLifeCycle {
	/**
	 * 在执行之前调用
	 */
	void beforeProcess() throws Throwable;

	/**
	 * 在执行之后调用
	 */
	void afterProcess() throws Throwable;

	/**
	 * 在事务回滚前调用
	 * 
	 * @throws Throwable
	 */
	void beforeRollback() throws Throwable;

	/**
	 * 在事务回滚后调用
	 * 
	 * @throws Throwable
	 */
	void afterRollback() throws Throwable;

	/**
	 * 事务结束后调用
	 */
	void complete() throws Throwable;
}
