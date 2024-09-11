package io.basc.framework.util.observe.future;

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.basc.framework.util.observe.Listener;
import io.basc.framework.util.observe.Registration;

public class ScheduledListenableFutureAdapter<V> implements ScheduledListenableFuture<V> {
	private final ListenableFuture<? extends V> listenableFuture;
	private final Delayed delayed;

	public ScheduledListenableFutureAdapter(ListenableFuture<? extends V> listenableFuture, Delayed delayed) {
		this.listenableFuture = listenableFuture;
		this.delayed = delayed;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return delayed.getDelay(unit);
	}

	@Override
	public int compareTo(Delayed o) {
		return delayed.compareTo(o);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return listenableFuture.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return listenableFuture.isCancelled();
	}

	@Override
	public boolean isDone() {
		return listenableFuture.isDone();
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		return listenableFuture.get();
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return listenableFuture.get(timeout, unit);
	}

	@Override
	public Registration registerListener(Listener<? super ListenableFuture<? extends V>> listener) {
		return listenableFuture.registerListener(listener);
	}

	@Override
	public V getNow() {
		return listenableFuture.getNow();
	}

	@Override
	public boolean isCancellable() {
		return listenableFuture.isCancellable();
	}

	@Override
	public Throwable cause() {
		return listenableFuture.cause();
	}

	@Override
	public boolean isSuccess() {
		return listenableFuture.isSuccess();
	}
}
