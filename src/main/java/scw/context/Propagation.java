package scw.context;

/**
 * 上下文的传播方式
 * 
 * @author shuchaowen
 *
 */
public enum Propagation {
	/**
	 * 默认的
	 * 如果存在一个上下文，则支持当前上下文。如果没有上下文则开启一个新的上下文
	 */
	REQUIRED,

	/**
	 * 如果存在一个上下文，支持当前上下文。如果没有上下文，则非上下文的执行。但是对于上下文同步的上下文管理器，
	 * PROPAGATION_SUPPORTS与不使用上下文有少许不同
	 */
	SUPPORTS,

	/**
	 * 如果已经存在一个上下文，支持当前上下文。如果没有一个活动的上下文，则抛出异常。
	 */
	MANDATORY,

	/**
	 * 总是开启一个新的上下文。如果一个上下文已经存在，则将这个存在的上下文挂起。
	 */
	REQUIRES_NEW,

	/**
	 * 总是非上下文地执行，并挂起任何存在的上下文
	 */
	NOT_SUPPORTED,

	/**
	 * 总是非上下文地执行，如果存在一个活动上下文，则抛出异常
	 */
	NEVER,

	/**
	 * 如果一个活动的上下文存在，则运行在一个嵌套的上下文中. 如果没有活动上下文,
	 * 则按TransactionDefinition.PROPAGATION_REQUIRED 属性执行
	 */
	NESTED
}
