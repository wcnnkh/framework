package io.basc.framework.observe;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.basc.framework.util.actor.batch.BatchEventDispatcher;
import io.basc.framework.util.observe.Observer;
import lombok.NonNull;

public abstract class PollingObserver<E> extends Observer<E> implements Polling {
	public PollingObserver(@NonNull BatchEventDispatcher<E> eventDispatcher) {
		super(eventDispatcher);
	}

	public boolean startEndlessLoop() {
		return startEndlessLoop(() -> {
			try {
				await();
			} catch (InterruptedException e) {
				return;
			}
			run();
		});
	}

	public boolean startScheduled(ScheduledExecutorService scheduledExecutorService) {
		return startScheduled(scheduledExecutorService, this);
	}

	public boolean startTimerTask() {
		return startTimerTask(this);
	}

	@Override
	public void await() throws InterruptedException {
		while (!await(getRefreshTimeCycle(), getRefreshTimeUnit()))
			;
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		long time = unit.toMillis(timeout);
		time = Math.min(time, getRefreshTimeUnit().toMillis(getRefreshTimeCycle()));
		Thread.sleep(time);
		return true;
	}
}
