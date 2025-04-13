package run.soeasy.framework.core.exchange.event;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

import run.soeasy.framework.core.exchange.AbstractChannel;
import run.soeasy.framework.core.exchange.ListenableChannel;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.register.container.ElementRegistration;
import run.soeasy.framework.core.register.container.QueueContainer;

/**
 * 一个listener只会交换一次数据
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class DisposableDispatcher<T> extends AbstractChannel<T> implements ListenableChannel<T> {
	private final QueueContainer<Listener<T>, Queue<ElementRegistration<Listener<T>>>> registry = new QueueContainer<>(
			LinkedList::new);

	@Override
	public Registration registerListener(Listener<T> listener) {
		return registry.register(listener);
	}

	@Override
	public void syncPublish(T resource) {
		Lock lock = registry.writeLock();
		lock.lock();
		try {
			Listener<? super T> listener;
			while ((listener = registry.peek()) != null) {
				listener.accept(resource);
				// 成功才会删除
				registry.remove();
			}
		} finally {
			lock.unlock();
		}
	}

	public void clear() {
		registry.clear();
	}
}
