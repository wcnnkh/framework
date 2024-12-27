package io.basc.framework.util.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import io.basc.framework.util.Registration;
import io.basc.framework.util.actor.ListenableFuture;
import io.basc.framework.util.actor.Stage;
import io.basc.framework.util.exchange.Listener;

public class ListenableFutureTask<T> extends FutureTask<T> implements ListenableFuture<T> {
	private Stage stage = new Stage();

	/**
	 * Create a new {@code ListenableFutureTask} that will, upon running, execute
	 * the given {@link Callable}.
	 * 
	 * @param callable the callable task
	 */
	public ListenableFutureTask(Callable<T> callable) {
		super(callable);
	}

	/**
	 * Create a {@code ListenableFutureTask} that will, upon running, execute the
	 * given {@link Runnable}, and arrange that {@link #get()} will return the given
	 * result on successful completion.
	 * 
	 * @param runnable the runnable task
	 * @param result   the result to return on successful completion
	 */
	public ListenableFutureTask(Runnable runnable, T result) {
		super(runnable, result);
	}

	@Override
	public Registration registerListener(Listener<? super ListenableFuture<? extends T>> listener) {
		return stage.registerListener((e) -> listener.accept(ListenableFutureTask.this));
	}

	@Override
	public boolean isCancellable() {
		return stage.isCancellable();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (super.cancel(mayInterruptIfRunning)) {
			stage.cancel();
			return true;
		}
		return false;
	}

	@Override
	public Throwable cause() {
		return stage.cause();
	}

	@Override
	public boolean isSuccess() {
		return stage.isSuccess();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getNow() {
		return (T) stage.getResult();
	}

	@Override
	protected void done() {
		Throwable cause;
		try {
			T result = get();
			this.stage.success(result);
			return;
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			return;
		} catch (ExecutionException ex) {
			cause = ex.getCause();
			if (cause == null) {
				cause = ex;
			}
		} catch (Throwable ex) {
			cause = ex;
		}
		this.stage.failure(cause);
	}
}
