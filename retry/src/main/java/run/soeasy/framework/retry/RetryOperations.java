package run.soeasy.framework.retry;

/**
 * Defines the basic set of operations implemented by {@link RetryOperations} to
 * execute operations with configurable retry behaviour.
 * 
 * @author wcnnkh
 *
 */
public interface RetryOperations {
	/**
	 * Execute the supplied {@link RetryCallback} with the configured retry
	 * semantics. See implementations for configuration details.
	 * 
	 * @param <T>           the return value
	 * @param retryCallback the {@link RetryCallback}
	 * @param <E>           the exception to throw
	 * @return the value returned by the {@link RetryCallback} upon successful
	 *         invocation.
	 */
	<T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback) throws E, ExhaustedRetryException;

	/**
	 * Execute the supplied {@link RetryCallback} with a fallback on exhausted retry
	 * to the {@link RecoveryCallback}. See implementations for configuration
	 * details.
	 * 
	 * @param recoveryCallback the {@link RecoveryCallback}
	 * @param retryCallback    the {@link RetryCallback} {@link RecoveryCallback}
	 *                         upon
	 * @param <T>              the type to return
	 * @return the value returned by the {@link RetryCallback} upon successful
	 *         invocation, and that returned by the {@link RecoveryCallback}
	 *         otherwise.
	 * @throws E any {@link Exception} raised by the unsuccessful retry.
	 */
	<T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback, RecoveryCallback<T, E> recoveryCallback)
			throws E;
}
