package run.soeasy.framework.core.exchange.future;

public interface Promise<V> extends Confirm, ListenableFuture<V> {
	/**
	 * Marks this future as a failure and notifies all listeners.
	 *
	 * If it is success or failed already it will throw an
	 * {@link IllegalStateException}.
	 */
	default Promise<V> setFailure(Throwable cause) {
		if (tryFailure(cause)) {
			return this;
		}
		throw new IllegalStateException();
	}

	/**
	 * Marks this future as a success and notifies all listeners.
	 *
	 * If it is success or failed already it will throw an
	 * {@link IllegalStateException}.
	 */
	default Promise<V> setSuccess(V result) {
		if (trySuccess(result)) {
			return this;
		}
		throw new IllegalStateException();
	}

	/**
	 * Make this future impossible to cancel.
	 *
	 * @return {@code true} if and only if successfully marked this future as
	 *         uncancellable or it is already done without being cancelled.
	 *         {@code false} if this future has been cancelled already.
	 */
	boolean setUncancellable();

	/**
	 * Marks this future as a failure and notifies all listeners.
	 *
	 * @return {@code true} if and only if successfully marked this future as a
	 *         failure. Otherwise {@code false} because this future is already
	 *         marked as either a success or a failure.
	 */
	boolean tryFailure(Throwable cause);

	@Override
	default boolean trySuccess() {
		return trySuccess(null);
	}

	/**
	 * Marks this future as a success and notifies all listeners.
	 *
	 * @return {@code true} if and only if successfully marked this future as a
	 *         success. Otherwise {@code false} because this future is already
	 *         marked as either a success or a failure.
	 */
	boolean trySuccess(V result);

	@Override
	default boolean cancel() {
		return ListenableFuture.super.cancel();
	}
}
