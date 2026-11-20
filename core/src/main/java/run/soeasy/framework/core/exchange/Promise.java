package run.soeasy.framework.core.exchange;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import lombok.NonNull;

public class Promise<V> extends CompletableFuture<V> implements ListenableStage<V> {
	protected static final Promise<Object> UNSUPPORTED_ROLLBACK = new Promise<Object>();
	protected static final Promise<Object> SUCCESS = new Promise<Object>();
	protected static final Promise<Object> FAILURE = new Promise<Object>();

	static {
		UNSUPPORTED_ROLLBACK.completeExceptionally(new UnsupportedOperationException("rollback"));
		SUCCESS.success(null);
		FAILURE.failure(new RuntimeException());
	}

	private final Function<? super V, ? extends Signal> rollback;
	private volatile Signal signal;
	private volatile DisposableDispatcher<Stage<V>> dispatcher;
	private volatile DisposableDispatcher<Stage<V>> successDispatcher;
	private volatile DisposableDispatcher<Stage<V>> failureDispatcher;

	public Promise() {
		this(null);
	}

	public Promise(Function<? super V, ? extends Signal> rollback) {
		this.rollback = rollback;
	}

	@Override
	public boolean isRollbackSupported() {
		return rollback != null;
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
		return UNSUPPORTED_ROLLBACK;
	}

	public Promise<V> success(V result) {
		if (complete(result)) {
			return this;
		}
		return new Promise<V>(this.rollback).success(result);
	}

	public Promise<V> failure(@NonNull Throwable cause) {
		if (completeExceptionally(cause)) {
			return this;
		}
		return new Promise<V>(this.rollback).failure(cause);
	}

	@Override
	public boolean complete(V value) {
		try {
			return super.complete(value);
		} finally {
			onSuccess(value);
		}
	}

	@Override
	public boolean completeExceptionally(Throwable ex) {
		try {
			return super.completeExceptionally(ex);
		} finally {
			onFailure(ex);
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

	public Promise<V> rollback(Function<? super V, ? extends Signal> rollback) {
		if (rollback == this.rollback || (rollback != null && rollback.equals(this.rollback))) {
			return this;
		}

		Promise<V> promise = new Promise<V>(rollback);
		if (this.isCancelled()) {
			promise.cancel(false);
		} else {
			if (this.isSuccess()) {
				promise.complete(getNow());
			} else {
				promise.completeExceptionally(cause());
			}
		}
		return promise;
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
