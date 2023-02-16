package io.basc.framework.transaction;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.Registration;

/**
 * 一个事务
 * 
 * @author shuchaowen
 *
 */
public interface Transaction extends SavepointManager, ParentDiscover<Transaction> {
	/**
	 * 获取事务的定义(配置)
	 * 
	 * @return
	 */
	TransactionDefinition getDefinition();

	Registration registerSynchronization(Synchronization synchronization) throws TransactionException;

	/**
	 * 获取指定名称的资源
	 * 
	 * @param name
	 * @return
	 */
	@Nullable
	<T> T getResource(Object name);

	/**
	 * 绑定一个资源
	 * 
	 * @see SavepointManager
	 * @param name
	 * @param resource
	 * @throws TransactionException
	 */
	Registration registerResource(Object name, Object resource) throws TransactionException;

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

	TransactionStatus getStatus();

	/**
	 * 是否已完成
	 * 
	 * @return
	 */
	boolean isCompleted();

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
}
