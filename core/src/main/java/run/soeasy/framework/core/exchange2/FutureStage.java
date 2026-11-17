package run.soeasy.framework.core.exchange2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class FutureStage<T> implements Stage<T> {
	@NonNull
	private final Future<? extends T> future;

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return future.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}

	@Override
	public boolean isRollbackSupported() {
		return false;
	}

	@Override
	public boolean isRolledBack() {
		return false;
	}

	@Override
	public Signal rollback() {
		return Signal.done(false);
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return future.get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return future.get(timeout, unit);
	}
}
