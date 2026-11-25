package run.soeasy.framework.core.exchange;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;

public interface Trace<V> extends Stage<V>, Future<V> {
	@Override
	default void await() throws InterruptedException, ExecutionException {
		get();
	}

	@Override
	default void await(long timeout, @NonNull TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		get(timeout, unit);
	}

	@Override
	boolean cancel(boolean mayInterruptIfRunning);

	default Throwable cause() {
		if (isDone()) {
			try {
				await();
			} catch (Throwable e) {
				return SignalException.unwrap(e);
			}
		}
		return null;
	}

	default V join() throws CompletionException {
		try {
			return get();
		} catch (Throwable e) {
			if (SignalException.unwrap(e) instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			throw new CompletionException(e);
		}
	}

	@Override
	default V join(long timeout, @NonNull TimeUnit unit) throws TimeoutException {
		try {
			return get(timeout, unit);
		} catch (Throwable e) {
			if (SignalException.unwrap(e) instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			throw new CompletionException(e);
		}
	}

	@Override
	default Trace<V> trace() {
		return this;
	}
}
