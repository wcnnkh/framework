package run.soeasy.framework.core.exchange;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

public interface SignalWrapper<W extends Signal> extends Signal, Wrapper<W> {
	@Override
	default void await() throws InterruptedException, ExecutionException {
		getSource().await();
	}

	@Override
	default void await(long timeout, @NonNull TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		getSource().await(timeout, unit);
	}

	@Override
	default boolean cancel(boolean mayInterruptIfRunning) {
		return getSource().cancel(mayInterruptIfRunning);
	}

	@Override
	default boolean isCancelled() {
		return getSource().isCancelled();
	}

	@Override
	default boolean isDone() {
		return getSource().isDone();
	}

	@Override
	default boolean isRollbackSupported() {
		return getSource().isRollbackSupported();
	}

	@Override
	default Signal rollback() {
		return getSource().rollback();
	}

	@Override
	default Signal and(@NonNull Signal... others) {
		return getSource().and(others);
	}

	@Override
	default Signal boxed() {
		return getSource().boxed();
	}

	@Override
	default Throwable cause() {
		return getSource().cause();
	}

	@Override
	default boolean isSuccess() {
		return getSource().isSuccess();
	}

	@Override
	default ListenableSignal<? extends Signal> listenable() {
		return getSource().listenable();
	}
}
