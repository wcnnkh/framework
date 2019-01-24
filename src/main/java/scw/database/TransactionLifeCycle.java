package scw.database;

/**
 * 事务的生命周期
 * 
 * @author shuchaowen
 *
 */
public interface TransactionLifeCycle {
	/**
	 * 在开始事务之前调用
	 */
	void before();

	/**
	 * 事务调用成功之后调用
	 */
	void after();

	/**
	 * 事务结束后调用
	 */
	void complete();
}
