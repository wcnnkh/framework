package io.basc.framework.transaction;

/**
 * 事务的定义
 * 
 * @author wcnnkh
 *
 */
public interface TransactionDefinition {
	public static final TransactionDefinition DEFAULT = new DefaultTransactionDefinition();

	Propagation getPropagation();

	Isolation getIsolation();

	int getTimeout();

	boolean isReadOnly();
}
