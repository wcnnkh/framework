package scw.transaction;

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
	void beforeProcess();

	/**
	 * 在执行之后调用
	 */
	void afterProcess();

	/**
	 * 在事务回滚前调用
	 * 
	 * @throws Throwable
	 */
	void beforeRollback();

	/**
	 * 在事务回滚后调用
	 * 
	 * @throws Throwable
	 */
	void afterRollback();

	/**
	 * 事务结束后调用
	 */
	void complete();
}
