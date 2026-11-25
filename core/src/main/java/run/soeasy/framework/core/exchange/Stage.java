package run.soeasy.framework.core.exchange;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import lombok.NonNull;

public interface Stage<V> extends Signal {
	default V getNow() {
		if (isDone()) {
			try {
				return join();
			} catch (Throwable e) {
			}
		}
		return null;
	}

	V join();

	V join(long timeout, @NonNull TimeUnit unit) throws TimeoutException;

	@Override
	default ListenableTrace<V> listenable() {
		return Signal.call(this::join);
	}

	default <R> Trace<R> map(@NonNull Function<? super V, ? extends R> mapper) {
		return new MappedTrace<>(this, mapper);
	}

	default Optional<V> quietly() {
		try {
			V value = join();
			return Optional.ofNullable(value);
		} catch (Throwable e) {
			if (SignalException.unwrap(e) instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			return Optional.empty();
		}
	}

	default Optional<V> quietly(long timeout, @NonNull TimeUnit unit) {
		try {
			V value = join(timeout, unit);
			return Optional.ofNullable(value);
		} catch (Throwable e) {
			if (SignalException.unwrap(e) instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			return Optional.empty();
		}
	}

	default Trace<V> trace() {
		return map(Function.identity());
	}

}
