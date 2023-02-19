package io.basc.framework.transaction;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ParentDiscover;

/**
 * 一个事务
 * 
 * @author shuchaowen
 *
 */
public interface Transaction extends Resource, ParentDiscover<Transaction> {
	/**
	 * 获取事务的定义(配置)
	 * 
	 * @return
	 */
	TransactionDefinition getDefinition();

	void registerSynchronization(Synchronization synchronization) throws TransactionException;

	/**
	 * 获取指定名称的资源
	 * 
	 * @param name
	 * @return
	 */
	@Nullable
	<T> T getResource(Object name);

	/**
	 * 注册一个资源
	 * 
	 * @see SavepointManager
	 * @param name
	 * @param resource
	 * @throws TransactionException
	 */
	void registerResource(Object name, Object resource) throws TransactionException;

	/**
	 * 事务是否是只回滚状态
	 * 
	 * @return
	 */
	boolean isRollbackOnly();

	/**
	 * 设置事务为只回滚
	 * 
	 * @return
	 */
	void setRollbackOnly() throws TransactionException;

	/**
	 * 是否是一个新的事务
	 * 
	 * @return
	 */
	boolean isNew();

	/**
	 * 事务是否是活跃的
	 * 
	 * @return
	 */
	boolean isActive();

	/**
	 * 是否存在保存点，即嵌套事务
	 * 
	 * @return
	 */
	boolean hasSavepoint();

	Status getStatus();
}
