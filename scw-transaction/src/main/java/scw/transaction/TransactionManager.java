package scw.transaction;

import scw.instance.InstanceUtils;
import scw.lang.Nullable;


/**
 * 事务管理器
 * @see ThreadLocalTransactionManager
 * @author shuchaowen
 *
 */
public interface TransactionManager {
	/**
	 * 全局的事务管理器
	 */
	public static final TransactionManager GLOBAL = InstanceUtils.loadService(TransactionManager.class, "scw.transaction.support.ThreadLocalTransactionManager");
	
	/**
	 * 获取当前事务
	 * @return
	 */
	@Nullable
	Transaction getTransaction();
	
	/**
	 * 当前是否存在事务
	 * @return
	 */
	boolean hasTransaction();
	
	/**
	 * 根据规则获取事务<br/>
	 * 默认{@link TransactionDefinition#DEFAULT}}
	 * @param transactionDefinition
	 * @return
	 * @throws TransactionException
	 */
	Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException;
	
	/**
	 * 提交一个事务
	 * @param transaction
	 * @throws Throwable
	 * @throws TransactionException
	 */
	void commit(Transaction transaction) throws Throwable, TransactionException;
	
	/**
	 * 回滚一个事务
	 * @param transaction
	 * @throws TransactionException
	 */
	void rollback(Transaction transaction) throws TransactionException;
}