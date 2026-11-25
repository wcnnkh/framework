package run.soeasy.framework.core.exchange;

import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import lombok.NonNull;

public interface Stage<V> extends Signal, Future<V> {
	@Override
	boolean cancel(boolean mayInterruptIfRunning);

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

	@Override
	default ListenableStage<V> listenable() {
		return Signal.call(() -> {
			try {
				return get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return null;
		});
	}

	default <R> Stage<R> map(@NonNull Function<? super V, ? extends R> mapper) {
		return new MappedStage<>(this, mapper);
	}

	default Optional<V> quietly() {
		try {
			V value = get();
			return Optional.ofNullable(value);
		} catch (Throwable e) {
			if (SignalException.unwrap(e) instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			return Optional.empty();
		}
	}

	default V join() throws CompletionException{
		try {
			return get();
		} catch (Throwable e) {
			if (SignalException.unwrap(e) instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			throw new CompletionException(e);
		}
	}
}
