package io.basc.framework.util.register.container;

import java.util.concurrent.locks.Lock;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.concurrent.LockableContainer;
import io.basc.framework.util.exchange.Publisher;
import io.basc.framework.util.exchange.event.ChangeEvent;
import io.basc.framework.util.function.Supplier;
import io.basc.framework.util.register.Container;
import io.basc.framework.util.register.PayloadRegistration;
import lombok.NonNull;

/**
 * 一个懒惰的容器定义
 * 
 * @author shuchaowen
 *
 * @param <C>
 */
public abstract class AbstractContainer<C, E, P extends PayloadRegistration<E>>
		extends LockableContainer<C, RuntimeException> implements Container<E, P> {
	private volatile Publisher<? super Elements<ChangeEvent<E>>> publisher = Publisher.empty();

	public AbstractContainer(@NonNull Supplier<? extends C, ? extends RuntimeException> containerSource) {
		super(containerSource);
	}

	public Publisher<? super Elements<ChangeEvent<E>>> getPublisher() {
		Lock lock = readLock();
		lock.lock();
		try {
			return publisher;
		} finally {
			lock.unlock();
		}
	}

	public void setPublisher(Publisher<? super Elements<ChangeEvent<E>>> publisher) {
		Lock lock = writeLock();
		lock.lock();
		try {
			this.publisher = publisher == null ? Publisher.empty() : publisher;
		} finally {
			lock.unlock();
		}
	}
}
