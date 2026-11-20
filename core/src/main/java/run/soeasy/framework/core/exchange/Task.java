package run.soeasy.framework.core.exchange;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Function;

public class Task<V> extends FutureTask<V> implements Stage<V> {
	private final Function<? super V, ? extends Signal> rollback;
	private volatile Signal signal;

	public Task(Callable<V> callable) {
		this(callable, null);
	}

	public Task(Callable<V> callable, Function<? super V, ? extends Signal> rollback) {
		super(callable);
		this.rollback = rollback;
	}

	public Task(Runnable runnable) {
		this(runnable, null, null);
	}

	public Task(Runnable runnable, V result, Function<? super V, ? extends Signal> rollback) {
		super(runnable, result);
		this.rollback = rollback;
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
		return Promise.UNSUPPORTED_ROLLBACK;
	}

	public Task<V> rollback(Function<? super V, ? extends Signal> rollback) {
		if (rollback == this.rollback || (rollback != null && rollback.equals(this.rollback))) {
			return this;
		}
		return new Task<>(this::get, rollback);
	}

	@Override
	public boolean isRollbackSupported() {
		return rollback != null;
	}
}
