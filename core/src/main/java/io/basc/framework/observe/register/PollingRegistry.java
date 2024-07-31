package io.basc.framework.observe.register;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.observe.Polling;
import io.basc.framework.util.register.PayloadBatchRegistration;
import io.basc.framework.util.register.Registration;

public abstract class PollingRegistry<E extends Polling> extends ObservableList<E> implements Polling {

	public PollingRegistry() {
		super();
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
	public void run() {
		getServices().forEach(Polling::run);
	}

	@Override
	public void await() throws InterruptedException {
		while (!await(getRefreshTimeCycle(), getRefreshTimeUnit()))
			;
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		List<E> list = getServices().toList();
		long time = unit.toMillis(timeout);
		time = Math.max(1, time / list.size());
		for (E polling : list) {
			if (polling.await(time, TimeUnit.MILLISECONDS)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected PayloadBatchRegistration<E> batch(PayloadBatchRegistration<E> batchRegistration) {
		refresh();
		return super.batch(batchRegistration).batch((es) -> () -> refresh());
	}

	public Registration registerBatchListener(BatchEventListener<RegistryEvent<E>> batchEventListener)
			throws EventRegistrationException {
		Registration registration = super.registerBatchListener(batchEventListener);
		refresh();
		return registration.and(() -> refresh());
	}

	private void refresh() {
		test((list) -> {
			if (list.size() == 0 || getListenerCount() == 0) {
				return stop();
			}

			if (list.size() > 0 && getListenerCount() > 0) {
				return start();
			}
			return false;
		});
	}

	public abstract boolean start();

	public abstract boolean stop();
}
