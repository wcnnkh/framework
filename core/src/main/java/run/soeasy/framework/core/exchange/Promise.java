package run.soeasy.framework.core.exchange;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import lombok.NonNull;

public class Promise<V> extends CompletableFuture<V> implements ListenableTrace<V> {
	protected static final Promise<Object> SUCCESS = new Promise<Object>();

	static {
		SUCCESS.complete(null);
	}

	private final Function<? super V, ? extends Signal> rollback;
	private volatile Signal signal;
	private volatile DisposableDispatcher<Trace<V>> dispatcher;
	private volatile DisposableDispatcher<Trace<V>> successDispatcher;
	private volatile DisposableDispatcher<Trace<V>> failureDispatcher;
	protected volatile Throwable cause;

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
		return Signal.failure();
	}

	@Override
	public Throwable cause() {
		return cause;
	}

	@Override
	public boolean complete(V value) {
		if (super.complete(value)) {
			onSuccess(value);
			return true;
		}
		return false;
	}

	@Override
	public boolean completeExceptionally(Throwable ex) {
		if (super.completeExceptionally(ex)) {
			this.cause = ex;
			onFailure(ex);
			return true;
		}
		return false;
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
	public Signal onSuccess(Listener<Trace<V>> listener) {
		if (isDone() && isSuccess()) {
			return Signal.run(() -> listener.accept(this));
		}

		if (successDispatcher == null) {
			synchronized (this) {
				if (successDispatcher == null) {
					successDispatcher = new DisposableDispatcher<Trace<V>>();
				}
			}
		}
		return successDispatcher.registerListener(listener);
	}

	@Override
	public Signal onFailure(Listener<Trace<V>> listener) {
		if (isDone() && !isSuccess()) {
			return Signal.run(() -> listener.accept(this));
		}

		if (failureDispatcher == null) {
			synchronized (this) {
				if (failureDispatcher == null) {
					failureDispatcher = new DisposableDispatcher<Trace<V>>();
				}
			}
		}
		return failureDispatcher.registerListener(listener);
	}

	@Override
	public Signal registerListener(@NonNull Listener<Trace<V>> listener) {
		if (isDone()) {
			return Signal.run(() -> listener.accept(this));
		}

		if (dispatcher == null) {
			synchronized (this) {
				if (dispatcher == null) {
					dispatcher = new DisposableDispatcher<Trace<V>>();
				}
			}
		}
		return dispatcher.registerListener(listener);
	}
}
