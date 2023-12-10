package io.basc.framework.observe.register;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.observe.Polling;
import io.basc.framework.util.Registration;

public abstract class PollingRegistry<E extends Polling> extends ElementRegistry<E> implements Polling {

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
	public ElementRegistration<E> register(E element) {
		ElementRegistration<E> registration = super.register(element);
		Lock writeLock = getReadWriteLock().writeLock();
		writeLock.lock();
		try {
			if (!isRunning() && getListenerCount() != 0) {
				// 如果有观察者尝试启动
				start();
			}
		} finally {
			writeLock.unlock();
		}
		return registration.and(() -> unregisterElement(element));
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<RegistryEvent<E>> batchEventListener)
			throws EventRegistrationException {
		Registration registration = super.registerBatchListener(batchEventListener);
		Lock writeLock = getReadWriteLock().writeLock();
		writeLock.lock();
		try {
			// 注册时尝试启动观察者
			if (!isRunning() && getSize() != 0) {
				// 如果存在元素，启动
				start();
			}
		} finally {
			writeLock.unlock();
		}
		return registration.and(() -> {
			if (isRunning() && getListenerCount() == 0) {
				// 没有观察者了，停止
				stop();
			}
		});
	}

	public abstract boolean start();

	public abstract boolean stop();

	private void unregisterElement(E element) {
		Lock writeLock = getReadWriteLock().writeLock();
		writeLock.lock();
		try {
			// 取消注册，判断是否还有元素需要观察，如果没有停止线程
			if (isRunning() && getSize() == 0) {
				stop();
			}
		} finally {
			writeLock.unlock();
		}
	}

}
