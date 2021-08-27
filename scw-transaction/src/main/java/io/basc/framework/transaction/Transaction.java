package io.basc.framework.transaction;

import io.basc.framework.lang.Nullable;

/**
 * 一个事务
 * @author shuchaowen
 *
 */
public interface Transaction {
	/**
	 * 获取事务的定义(配置)
	 * @return
	 */
	TransactionDefinition getDefinition();
	
	/**
	 * 添加事务的生命周期
	 * @param lifecycle
	 */
	void addLifecycle(TransactionLifecycle lifecycle) throws TransactionException;
	
	/**
	 * 获取指定名称的资源
	 * @param name
	 * @return
	 */
	@Nullable
	<T> T getResource(Object name);
	
	/**
	 * 绑定一个资源
	 * @param name
	 * @param resource
	 * @return 返回关联资源，如果资源已经存在就返回已存在的资源，如果资源不存在就返回null
	 * @throws TransactionException
	 */
	@Nullable
	<T> T bindResource(Object name, T resource) throws TransactionException;
	
	/**
	 * 事务是否是只回滚状态
	 * @return
	 */
	boolean isRollbackOnly();
	
	/**
	 * 设置事务的回滚状态
	 * @param rollbackOnly
	 * @return 是否设置成功
	 */
	boolean setRollbackOnly(boolean rollbackOnly) throws TransactionException;
	
	/**
	 * 是否是一个新的事务
	 * @return
	 */
	boolean isNew();
	
	/**
	 * 事务是否是活跃的
	 * @return
	 */
	boolean isActive();
	
	/**
	 * 事务是否已完成(结束)
	 * @return
	 */
	boolean isCompleted();
	
	/**
	 * 是否存在保存点，即嵌套事务
	 * @return
	 */
	boolean hasSavepoint();
}
