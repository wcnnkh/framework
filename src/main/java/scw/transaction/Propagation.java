package scw.transaction;

/**
 * 事务的传播方式
 * 
 * @author shuchaowen
 *
 */
public enum Propagation {
	/**
	 * 默认的
	 * 如果存在一个事务，则支持当前事务。如果没有事务则开启一个新的事务
	 */
	REQUIRED,

	/**
	 * 如果存在一个事务，支持当前事务。如果没有事务，则非事务的执行。但是对于事务同步的事务管理器，
	 * PROPAGATION_SUPPORTS与不使用事务有少许不同
	 */
	SUPPORTS,

	/**
	 * 如果已经存在一个事务，支持当前事务。如果没有一个活动的事务，则抛出异常。
	 */
	MANDATORY,

	/**
	 * 总是开启一个新的事务。如果一个事务已经存在，则将这个存在的事务挂起。
	 */
	REQUIRES_NEW,

	/**
	 * 总是非事务地执行，并挂起任何存在的事务
	 */
	NOT_SUPPORTED,

	/**
	 * 总是非事务地执行，如果存在一个活动事务，则抛出异常
	 */
	NEVER,

	/**
	 * 如果一个活动的事务存在，则运行在一个嵌套的事务中. 如果没有活动事务,
	 * 则按TransactionDefinition.PROPAGATION_REQUIRED 属性执行
	 */
	NESTED
}
