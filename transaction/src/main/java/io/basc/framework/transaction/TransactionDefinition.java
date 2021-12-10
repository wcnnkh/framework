package io.basc.framework.transaction;

/**
 * 事务的定义
 * 
 * @author shuchaowen
 *
 */
public interface TransactionDefinition {
	public static final TransactionDefinition DEFAULT = new DefaultTransactionDefinition();

	/**
	 * 事务传播方式
	 * 
	 * @return
	 */
	Propagation getPropagation();

	/**
	 * 事务隔离级别
	 * 
	 * @return
	 */
	Isolation getIsolation();

	/**
	 * 超时时间(秒)
	 * 
	 * @return
	 */
	int getTimeout();

	/**
	 * 是否是只读事务
	 * 
	 * @return
	 */
	boolean isReadOnly();
}
