package run.soeasy.framework.core.exchange;

import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;

public interface Trace<V> extends Stage<V>, Future<V> {
	default Throwable cause() {
		if (isDone()) {
			try {
				join();
			} catch (Throwable e) {
				return e;
			}
		}
		return null;
	}

	@Override
	default boolean isSuccess() {
		return isDone() && cause() == null;
	}

	@Override
	default V join() {
		try {
			return get();
		} catch (Throwable e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			throw new CompletionException(e);
		}
	}

	@Override
	default V join(long timeout, @NonNull TimeUnit unit) throws TimeoutException {
		try {
			return get(timeout, unit);
		} catch (TimeoutException e) {
			throw e;
		} catch (Throwable e) {
			if (e instanceof InterruptedException) {
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
