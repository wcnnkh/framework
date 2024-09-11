package io.basc.framework.util.observe.event;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

import io.basc.framework.util.observe.Listener;
import io.basc.framework.util.observe.Publisher;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.register.container.ElementRegistration;
import io.basc.framework.util.observe.register.container.QueueRegistry;

/**
 * 一个listener只会接收一次事件
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class ListenerQueue<T> extends AbstractExchange<T> {
	private final QueueRegistry<Listener<? super T>, Queue<ElementRegistration<Listener<? super T>>>> registry = new QueueRegistry<>(
			LinkedList::new, Publisher.empty());

	@Override
	public Registration registerListener(Listener<? super T> listener) {
		return registry.register(listener);
	}

	@Override
	public void syncPublish(T resource) {
		Lock lock = registry.getReadWriteLock().writeLock();
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
