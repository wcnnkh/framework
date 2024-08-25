package io.basc.framework.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface DelayableExecutor extends AsyncExecutor {

	default <V> Future<V> submit(Callable<V> task) throws RejectedExecutionException {
		return schedule(task, 0, TimeUnit.SECONDS);
	};

	default ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit delayTimeUnit) {
		return schedule(Executors.callable(command), delay, delayTimeUnit);
	}

	<V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit delayTimeUnit);

	default ScheduledListenableFuture<?> scheduleListenable(Runnable command, long delay, TimeUnit delayTimeUnit) {
		ListenableFutureTask<Object> future = new ListenableFutureTask<>(command, null);
		Delayed scheduledFuture = schedule(future, delay, delayTimeUnit);
		return new ScheduledListenableFutureAdapter<>(future, scheduledFuture);
	}

	default <V> ScheduledListenableFuture<V> scheduleListenable(Callable<V> callable, long delay,
			TimeUnit delayTimeUnit) {
		ListenableFutureTask<V> future = new ListenableFutureTask<V>(callable);
		ScheduledFuture<?> scheduledFuture = schedule(future, delay, delayTimeUnit);
		return new ScheduledListenableFutureAdapter<V>(future, scheduledFuture);
	}
}
