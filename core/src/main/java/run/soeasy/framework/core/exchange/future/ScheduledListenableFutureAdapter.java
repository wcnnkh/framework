package run.soeasy.framework.core.exchange.future;

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Registration;

@RequiredArgsConstructor
public class ScheduledListenableFutureAdapter<V> implements ScheduledListenableFuture<V> {
	@NonNull
	private final ListenableFuture<V> listenableFuture;
	@NonNull
	private final Delayed delayed;

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
	public Registration registerListener(Listener<ListenableFuture<? extends V>> listener) {
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
