package run.soeasy.framework.core.exchange;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Option<V> implements Stage<V> {
	private final boolean success;
	private final V value;
	private final Function<? super V, ? extends Signal> rollback;
	private volatile Signal signal;

	public Option() {
		this(false, null, null);
	}

	public Option(V value) {
		this(true, value, null);
	}

	public Option(V value, Function<? super V, ? extends Signal> rollback) {
		this(true, value, rollback);
	}

	@Override
	public void await() {
	}

	@Override
	public void await(long timeout, @NonNull TimeUnit unit) {
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public boolean isRollbackSupported() {
		return success && rollback != null;
	}

	@Override
	public Signal rollback() {
		if (rollback != null && isSuccess()) {
			if (signal == null) {
				synchronized (this) {
					if (signal == null) {
						signal = rollback.apply(getNow());
					}
				}
			}
			return signal;
		}
		return Signal.failure();
	}

	@Override
	public V join() {
		if (success) {
			return value;
		}
		throw new NoSuchElementException("No value present");
	}

	@Override
	public V join(long timeout, @NonNull TimeUnit unit) throws TimeoutException {
		if (success) {
			return value;
		}
		throw new NoSuchElementException("No value present");
	}

	@Override
	public V getNow() {
		return value;
	}

	@Override
	public Optional<V> quietly() {
		return Optional.ofNullable(value);
	}

	@Override
	public Optional<V> quietly(long timeout, @NonNull TimeUnit unit) {
		return Optional.ofNullable(value);
	}

	public Option<V> rollback(Function<? super V, ? extends Signal> rollback) {
		if (rollback == this.rollback || (rollback != null && rollback.equals(this.rollback))) {
			return this;
		}

		return new Option<V>(this.success, this.value, rollback);
	}
}
