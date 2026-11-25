package run.soeasy.framework.core.exchange;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.streaming.Streamable;

public interface Signals<T extends Signal> extends Signal {
	public static interface CompositePattern {
		<E extends Signal> boolean cancel(@NonNull Streamable<E> elements, boolean mayInterruptIfRunning);

		<E extends Signal> boolean isCancelled(@NonNull Streamable<E> elements);

		<E extends Signal> boolean isDone(@NonNull Streamable<E> elements);

		<E extends Signal> boolean isSuccess(@NonNull Streamable<E> elements);

		<E extends Signal> void await(@NonNull Streamable<E> elements) throws InterruptedException, ExecutionException;

		<E extends Signal> void await(@NonNull Streamable<E> elements, long timeout, @NonNull TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException;

		<E extends Signal> boolean isRollbackSupported(@NonNull Streamable<E> elements);

		default <E extends Signal> Signal rollback(@NonNull Streamable<E> elements) {
			List<Signal> list = elements.map(Signal::rollback).toList();
			return new CompositeSignals<>(this, Streamable.of(list));
		}
	}

	CompositePattern getCompositePattern();

	Streamable<T> getElements();

	@RequiredArgsConstructor
	@Getter
	public static class CompositeSignals<E extends Signal, C extends CompositePattern> implements Signals<E> {
		private final C compositePattern;
		private final Streamable<E> elements;
	}

	@Override
	default boolean cancel(boolean mayInterruptIfRunning) {
		return getCompositePattern().cancel(getElements(), mayInterruptIfRunning);
	}

	@Override
	default boolean isRollbackSupported() {
		return getCompositePattern().isRollbackSupported(getElements());
	}

	@Override
	default void await() throws InterruptedException, ExecutionException {
		getCompositePattern().await(getElements());
	}

	@Override
	default void await(long timeout, @NonNull TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		getCompositePattern().await(getElements(), timeout, unit);
	}

	@Override
	default boolean isCancelled() {
		return getCompositePattern().isCancelled(getElements());
	}

	@Override
	default boolean isDone() {
		return getCompositePattern().isDone(getElements());
	}

	@Override
	default boolean isSuccess() {
		return getCompositePattern().isSuccess(getElements());
	}

	@Override
	default Signal rollback() {
		return getCompositePattern().rollback(getElements());
	}

	@Override
	default Signal boxed() {
		return new CompositeSignals<>(getCompositePattern(), Streamable.singleton(this));
	}
}
