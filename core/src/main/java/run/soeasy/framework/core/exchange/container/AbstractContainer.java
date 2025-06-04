package run.soeasy.framework.core.exchange.container;

import java.util.concurrent.locks.Lock;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.concurrent.LockableContainer;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * 一个懒惰的容器定义
 * 
 * @author shuchaowen
 *
 * @param <C>
 */
public abstract class AbstractContainer<C, E, P extends PayloadRegistration<E>>
		extends LockableContainer<C, RuntimeException> implements Container<E, P> {
	private volatile Publisher<? super Elements<ChangeEvent<E>>> publisher = Publisher.ignore();

	public AbstractContainer(@NonNull ThrowingSupplier<? extends C, ? extends RuntimeException> containerSource) {
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

	public void setPublisher(@NonNull Publisher<? super Elements<ChangeEvent<E>>> publisher) {
		Lock lock = writeLock();
		lock.lock();
		try {
			this.publisher = publisher;
		} finally {
			lock.unlock();
		}
	}
}
