package run.soeasy.framework.core.exchange;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.NonNull;

public interface Stage<V> extends Signal {
	default <U> Stage<U> flatMap(@NonNull Function<? super V, ? extends Stage<U>> mapper) {
		return new FlatMapStage<>(this, mapper);
	}

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

	default Trace<V> peek(@NonNull Consumer<? super V> consumer) {
		return map((e) -> {
			consumer.accept(e);
			return e;
		});
	}

	default Optional<V> quietly() {
		try {
			V value = join();
			return Optional.ofNullable(value);
		} catch (Throwable e) {
			if (e instanceof InterruptedException) {
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
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			return Optional.empty();
		}
	}

	@Override
	default Stage<V> sync() {
		try {
			join();
		} catch (Throwable e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
		}
		return this;
	}

	@Override
	default Stage<V> sync(long timeout, @NonNull TimeUnit unit) throws TimeoutException {
		try {
			join(timeout, unit);
		} catch (Throwable e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
		}
		return this;
	}

	default Trace<V> trace() {
		return map(Function.identity());
	}
}
