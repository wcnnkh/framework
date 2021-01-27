package scw.retry;

/**
 * Defines the basic set of operations implemented by {@link RetryOperations} to
 * execute operations with configurable retry behaviour.
 * 
 * @author shuchaowen
 *
 */
public interface RetryOperations {
	/**
	 * Execute the supplied {@link RetryCallback} with the configured retry
	 * semantics. See implementations for configuration details.
	 * 
	 * @param <T>
	 *            the return value
	 * @param retryCallback
	 *            the {@link RetryCallback}
	 * @param <E>
	 *            the exception to throw
	 * @return the value returned by the {@link RetryCallback} upon successful
	 *         invocation.
	 * @throws E
	 *             any {@link Exception} raised by the {@link RetryCallback}
	 *             upon unsuccessful retry.
	 * @throws E
	 *             the exception thrown
	 */
	<T> T execute(RetryCallback<T> retryCallback)
			throws Throwable;

	/**
	 * Execute the supplied {@link RetryCallback} with a fallback on exhausted
	 * retry to the {@link RecoveryCallback}. See implementations for
	 * configuration details.
	 * 
	 * @param recoveryCallback
	 *            the {@link RecoveryCallback}
	 * @param retryCallback
	 *            the {@link RetryCallback} {@link RecoveryCallback} upon
	 * @param <T>
	 *            the type to return
	 * @return the value returned by the {@link RetryCallback} upon successful
	 *         invocation, and that returned by the {@link RecoveryCallback}
	 *         otherwise.
	 * @throws Throwable
	 *             any {@link Exception} raised by the unsuccessful retry.
	 */
	<T> T execute(RetryCallback<T> retryCallback,
			RecoveryCallback<T> recoveryCallback) throws Throwable;

	/**
	 * A simple stateful retry. Execute the supplied {@link RetryCallback} with
	 * a target object for the attempt identified by the
	 * {@link DefaultRetryState}. Exceptions thrown by the callback are always
	 * propagated immediately so the state is required to be able to identify
	 * the previous attempt, if there is one - hence the state is required.
	 * Normal patterns would see this method being used inside a transaction,
	 * where the callback might invalidate the transaction if it fails.
	 *
	 * See implementations for configuration details.
	 * 
	 * @param retryCallback
	 *            the {@link RetryCallback}
	 * @param retryState
	 *            the {@link RetryState}
	 * @param <T>
	 *            the type of the return value
	 * @return the value returned by the {@link RetryCallback} upon successful
	 *         invocation, and that returned by the {@link RecoveryCallback}
	 *         otherwise.
	 * @throws Throwable
	 *             any {@link Exception} raised by the {@link RecoveryCallback}.
	 * @throws ExhaustedRetryException
	 *             if the last attempt for this state has already been reached
	 */
	<T> T execute(RetryCallback<T> retryCallback,
			RetryState retryState) throws Throwable, ExhaustedRetryException;

	/**
	 * A stateful retry with a recovery path. Execute the supplied
	 * {@link RetryCallback} with a fallback on exhausted retry to the
	 * {@link RecoveryCallback} and a target object for the retry attempt
	 * identified by the {@link DefaultRetryState}.
	 * 
	 * @param recoveryCallback
	 *            the {@link RecoveryCallback}
	 * @param retryState
	 *            the {@link RetryState}
	 * @param retryCallback
	 *            the {@link RetryCallback}
	 * @param <T>
	 *            the return value type
	 * @see #execute(RetryCallback, RetryState)
	 * @return the value returned by the {@link RetryCallback} upon successful
	 *         invocation, and that returned by the {@link RecoveryCallback}
	 *         otherwise.
	 * @throws Throwable
	 *             any {@link Exception} raised by the {@link RecoveryCallback}
	 *             upon unsuccessful retry.
	 */
	<T> T execute(RetryCallback<T> retryCallback,
			RecoveryCallback<T> recoveryCallback, RetryState retryState)
			throws Throwable;
}
