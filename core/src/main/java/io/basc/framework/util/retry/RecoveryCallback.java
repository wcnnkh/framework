package io.basc.framework.util.retry;

/**
 * 在所有尝试都已完成后回调以进行有状态重试 Callback for stateful retry after all tries are
 * exhausted.
 * 
 * @author wcnnkh
 *
 * @param <T>
 * @param <E>
 */
@FunctionalInterface
public interface RecoveryCallback<T, E extends Throwable> {
	T recover(RetryContext context) throws E;
}
