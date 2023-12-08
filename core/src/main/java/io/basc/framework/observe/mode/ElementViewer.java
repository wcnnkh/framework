package io.basc.framework.observe.mode;

import java.util.concurrent.locks.Lock;

import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.observe.register.ElementRegistration;
import io.basc.framework.observe.register.ElementRegistry;
import io.basc.framework.observe.register.RegistryEvent;
import io.basc.framework.util.Registration;

public abstract class ElementViewer<E> extends ElementRegistry<E> implements Viewer {

	@Override
	public void await() throws InterruptedException {
		while (!await(getListenerCount(), getRefreshTimeUnit()))
			;
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
