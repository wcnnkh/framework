package run.soeasy.framework.util.register.container;

import java.util.concurrent.locks.Lock;

import lombok.NonNull;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.concurrent.LockableContainer;
import run.soeasy.framework.util.exchange.Publisher;
import run.soeasy.framework.util.exchange.event.ChangeEvent;
import run.soeasy.framework.util.function.Supplier;
import run.soeasy.framework.util.register.Container;
import run.soeasy.framework.util.register.PayloadRegistration;

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
