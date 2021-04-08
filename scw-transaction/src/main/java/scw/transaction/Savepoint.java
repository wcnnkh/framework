package scw.transaction;

/**
  *  保存点
 * @author shuchaowen
 *
 */
public interface Savepoint {
	void rollback() throws TransactionException;

	void release() throws TransactionException;
}
