package io.basc.framework.util.exchange.event;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

import io.basc.framework.util.exchange.AbstractChannel;
import io.basc.framework.util.exchange.ListenableChannel;
import io.basc.framework.util.exchange.Listener;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.register.container.ElementRegistration;
import io.basc.framework.util.register.container.QueueContainer;

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
