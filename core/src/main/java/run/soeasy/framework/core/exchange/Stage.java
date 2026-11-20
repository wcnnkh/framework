package run.soeasy.framework.core.exchange;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;

public interface Stage<V> extends Signal, Future<V> {
	@Override
	default void await() throws InterruptedException, ExecutionException {
		get();
	}

	@Override
	default void await(long timeout, @NonNull TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		get(timeout, unit);
	}

	default V getNow() {
		if (isDone()) {
			try {
				return get();
			} catch (Throwable e) {
			}
		}
		return null;
	}
}
