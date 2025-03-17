package run.soeasy.framework.util.concurrent;

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
	public void addCallback(ListenableFutureCallback<? super V> callback) {
		listenableFuture.addCallback(callback);
	}

	@Override
	public void addCallback(SuccessCallback<? super V> successCallback, FailureCallback failureCallback) {
		listenableFuture.addCallback(successCallback, failureCallback);
	}
}
