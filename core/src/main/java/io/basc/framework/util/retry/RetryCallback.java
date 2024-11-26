package io.basc.framework.util.retry;

@FunctionalInterface
public interface RetryCallback<T, E extends Throwable> {
	/**
	 * Execute an operation with retry semantics. Operations should generally be
	 * idempotent, but implementations may choose to implement compensation
	 * semantics when an operation is retried.
	 * 
	 * @param context the current retry context.
	 * @return the result of the successful operation.
	 * @throws E if processing fails
	 */
	T doWithRetry(RetryContext context) throws E;
}
