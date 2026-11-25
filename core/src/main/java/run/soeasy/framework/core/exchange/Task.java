package run.soeasy.framework.core.exchange;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Function;

import lombok.NonNull;

public class Task<V> extends FutureTask<V> implements ListenableStage<V> {
	private final Function<? super V, ? extends Signal> rollback;
	private volatile Signal signal;
	private volatile DisposableDispatcher<Stage<V>> dispatcher;
	private volatile DisposableDispatcher<Stage<V>> successDispatcher;
	private volatile DisposableDispatcher<Stage<V>> failureDispatcher;

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
		return Signal.failure(new UnsupportedOperationException());
	}

	public Task<V> rollback(Function<? super V, ? extends Signal> rollback) {
		if (rollback == this.rollback || (rollback != null && rollback.equals(this.rollback))) {
			return this;
		}
		return new Task<>(this::get, rollback);
	}

	@Override
	protected void set(V v) {
		try {
			super.set(v);
		} finally {
			onSuccess(v);
		}
	}

	@Override
	protected void setException(Throwable t) {
		try {
			super.setException(t);
		} finally {
			onFailure(t);
		}

	}

	protected synchronized void onSuccess(V value) {
		if (successDispatcher != null) {
			successDispatcher.publish(this);
		}
		if (dispatcher != null) {
			dispatcher.publish(this);
		}
	}

	protected synchronized void onFailure(Throwable ex) {
		if (failureDispatcher != null) {
			failureDispatcher.publish(this);
		}
		if (dispatcher != null) {
			dispatcher.publish(this);
		}
	}

	@Override
	public synchronized boolean isRollbackSupported() {
		return rollback != null;
	}

	@Override
	public Signal registerSuccessListener(Listener<Stage<V>> listener) {
		if (isDone() && isSuccess()) {
			return Signal.run(() -> listener.accept(this));
		}

		if (successDispatcher == null) {
			synchronized (this) {
				if (successDispatcher == null) {
					successDispatcher = new DisposableDispatcher<Stage<V>>();
				}
			}
		}
		return successDispatcher.registerListener(listener);
	}

	@Override
	public Signal registerFailureListener(Listener<Stage<V>> listener) {
		if (isDone() && !isSuccess()) {
			return Signal.run(() -> listener.accept(this));
		}

		if (failureDispatcher == null) {
			synchronized (this) {
				if (failureDispatcher == null) {
					failureDispatcher = new DisposableDispatcher<Stage<V>>();
				}
			}
		}
		return failureDispatcher.registerListener(listener);
	}

	@Override
	public Signal registerListener(@NonNull Listener<Stage<V>> listener) {
		if (isDone()) {
			return Signal.run(() -> listener.accept(this));
		}

		if (dispatcher == null) {
			synchronized (this) {
				if (dispatcher == null) {
					dispatcher = new DisposableDispatcher<Stage<V>>();
				}
			}
		}
		return dispatcher.registerListener(listener);
	}
}
