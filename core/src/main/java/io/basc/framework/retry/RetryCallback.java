package io.basc.framework.retry;

@FunctionalInterface
public interface RetryCallback<T, E extends Throwable> {
	/**
	 * Execute an operation with retry semantics. Operations should generally be
	 * idempotent, but implementations may choose to implement compensation semantics when
	 * an operation is retried.
	 * @param context the current retry context.
	 * @return the result of the successful operation.
	 * @throws Throwable if processing fails
	 */
	T doWithRetry(RetryContext context) throws E;
}
