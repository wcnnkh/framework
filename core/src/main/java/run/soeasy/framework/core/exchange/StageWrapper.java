package run.soeasy.framework.core.exchange;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.NonNull;

public interface StageWrapper<V, W extends Stage<V>> extends SignalWrapper<W>, Stage<V> {

	@Override
	default boolean cancel(boolean mayInterruptIfRunning) {
		return getSource().cancel(mayInterruptIfRunning);
	}

	@Override
	default Stage<V> sync() {
		return getSource().sync();
	}

	@Override
	default Stage<V> sync(long timeout, @NonNull TimeUnit unit) throws TimeoutException {
		return getSource().sync(timeout, unit);
	}

	@Override
	default ListenableTrace<V> listenable() {
		return getSource().listenable();
	}

	@Override
	default boolean isDone() {
		return getSource().isDone();
	}

	@Override
	default boolean isCancelled() {
		return getSource().isCancelled();
	}

	@Override
	default <U> Stage<U> flatMap(@NonNull Function<? super V, ? extends Stage<U>> mapper) {
		return getSource().flatMap(mapper);
	}

	@Override
	default V getNow() {
		return getSource().getNow();
	}

	@Override
	default V join() {
		return getSource().join();
	}

	@Override
	default V join(long timeout, @NonNull TimeUnit unit) throws TimeoutException {
		return getSource().join(timeout, unit);
	}

	@Override
	default <R> Trace<R> map(@NonNull Function<? super V, ? extends R> mapper) {
		return getSource().map(mapper);
	}

	@Override
	default Trace<V> peek(@NonNull Consumer<? super V> consumer) {
		return getSource().peek(consumer);
	}

	@Override
	default Optional<V> quietly() {
		return getSource().quietly();
	}

	@Override
	default Trace<V> trace() {
		return getSource().trace();
	}

	@Override
	default Optional<V> quietly(long timeout, @NonNull TimeUnit unit) {
		return getSource().quietly(timeout, unit);
	}
}
