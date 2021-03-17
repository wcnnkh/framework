package scw.transaction;


/**
 * 事务的定义
 * @author shuchaowen
 *
 */
public interface TransactionDefinition {
	public static final TransactionDefinition DEFAULT = new DefaultTransactionDefinition();
	
	Propagation getPropagation();

	Isolation getIsolation();

	int getTimeout();

	boolean isReadOnly();
}
