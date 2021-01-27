package scw.context.transaction;

/**
 * 对于方法返回值回滚判定
 * @author shuchaowen
 *
 */
public interface RollbackOnlyResult {
	
	/**
	 * 是否应该将当前事务设置为只回滚
	 * @return
	 */
	boolean isRollbackOnly();
	
}
