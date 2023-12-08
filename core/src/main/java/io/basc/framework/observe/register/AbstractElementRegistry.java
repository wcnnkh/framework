package io.basc.framework.observe.register;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.basc.framework.util.element.Elements;

public abstract class AbstractElementRegistry<E> extends AbstractRegistry<E> {
	private E firstService;
	private E lastService;
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	public E getFirstService() {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return firstService;
		} finally {
			lock.unlock();
		}
	}

	public E getLastService() {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return lastService;
		} finally {
			lock.unlock();
		}
	}

	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	@Override
	public Elements<E> getServices() {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			Elements<E> elements = Elements.empty();
			if (firstService != null) {
				elements = elements.concat(Elements.singleton(firstService));
			}

			elements = elements.concat(loadServices());

			if (lastService != null) {
				elements = elements.concat(Elements.singleton(lastService));
			}

			return elements;
		} finally {
			lock.unlock();
		}
	}

	protected abstract Elements<E> loadServices();

	public void setFirstService(E firstService) {
		Lock writeLock = readWriteLock.writeLock();
		writeLock.lock();
		try {
			if (this.firstService == null) {
				if (firstService == null) {
					// 无变更
					return;
				} else {
					this.firstService = firstService;
					publishEvent(new RegistryEvent<>(this, RegistryEventType.REGISTER, firstService));
				}
			} else {
				if (firstService == null) {
					E oldService = this.firstService;
					this.firstService = null;
					publishEvent(new RegistryEvent<>(this, RegistryEventType.UNREGISTER, oldService));
				} else {
					E oldService = this.firstService;
					this.firstService = firstService;
					publishEvent(new RegistryEvent<>(this, RegistryEventType.UPDATE, oldService));
				}
			}
		} finally {
			writeLock.unlock();
		}
	}

	public void setLastService(E lastService) {
		Lock writeLock = readWriteLock.writeLock();
		writeLock.lock();
		try {
			if (this.lastService == null) {
				if (lastService == null) {
					// 无变更
					return;
				} else {
					this.lastService = lastService;
					publishEvent(new RegistryEvent<>(this, RegistryEventType.REGISTER, firstService));
				}
			} else {
				if (lastService == null) {
					E oldService = this.lastService;
					this.lastService = null;
					publishEvent(new RegistryEvent<>(this, RegistryEventType.UNREGISTER, oldService));
				} else {
					E oldService = this.lastService;
					this.lastService = lastService;
					publishEvent(new RegistryEvent<>(this, RegistryEventType.UPDATE, oldService));
				}
			}
		} finally {
			writeLock.unlock();
		}
	}
}
