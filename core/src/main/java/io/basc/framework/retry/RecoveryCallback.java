package io.basc.framework.retry;

/**
 * 
 * 在所有尝试都已完成后回调以进行有状态重试 Callback for stateful retry after all tries are
 * exhausted.
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
@FunctionalInterface
public interface RecoveryCallback<T, E extends Throwable> {
	/**
	 * @param context the current retry context
	 * @return an Object that can be used to replace the callback result that failed
	 * @throws Exception when something goes wrong
	 */
	T recover(RetryContext context) throws E;
}
