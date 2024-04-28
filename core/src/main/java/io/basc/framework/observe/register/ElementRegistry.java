package io.basc.framework.observe.register;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import io.basc.framework.observe.ChangeType;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;
import io.basc.framework.util.RegistrationException;
import io.basc.framework.util.Registrations;
import io.basc.framework.util.VersionRegistration;
import io.basc.framework.util.element.Elements;

public class ElementRegistry<E> extends AbstractElementRegistry<E> {
	private volatile long version;
	private volatile Collection<ElementRegistration<E>> registrations;

	public Elements<ElementRegistration<E>> getRegistrations() {
		Lock lock = getReadWriteLock().readLock();
		lock.lock();
		try {
			if (registrations == null) {
				return Elements.empty();
			}

			// copy保证线程安全
			List<ElementRegistration<E>> list = new ArrayList<>(registrations);
			return Elements.of(list);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Registrations<ElementRegistration<E>> clear() throws RegistrationException {
		Lock writeLock = getReadWriteLock().writeLock();
		writeLock.lock();
		try {
			List<ElementRegistration<E>> elements = registrations == null ? Collections.emptyList()
					: new ArrayList<>(registrations);
			registrations = null;
			this.version++;
			List<ElementRegistration<E>> registrations = elements.stream().map((e) -> {
				VersionRegistration versionRegistration = new VersionRegistration(() -> this.version,
						() -> unregister(e, ChangeType.CREATE));
				return new ElementRegistration<E>(e.getElement(), versionRegistration);
			}).collect(Collectors.toList());
			return new Registrations<>(Elements.of(registrations));
		} finally {
			writeLock.unlock();
		}
	}

	public boolean contains(E element) {
		return getServices().contains(element);
	}

	public long getVersion() {
		return version;
	}

	@Override
	protected Elements<E> loadServices() {
		if (registrations == null) {
			return Elements.empty();
		}
		return Elements.of(registrations.stream().map((e) -> e.getElement()).collect(Collectors.toList()));
	}

	public void register(ElementRegistration<E> elementRegistration) {
		Lock writeLock = getReadWriteLock().writeLock();
		writeLock.lock();
		try {
			if (registrations == null) {
				registrations = createRegistrations();
			}

			if (registrations.add(elementRegistration)) {
				Registration registration = new VersionRegistration(() -> this.version,
						() -> unregister(elementRegistration, ChangeType.DELETE));
				elementRegistration.add(registration);
			}
		} finally {
			writeLock.unlock();

			if (!elementRegistration.isInvalid()) {
				publishEvent(new RegistryEvent<>(this, ChangeType.CREATE, elementRegistration.getElement()));
			}
		}
	}

	public ElementRegistration<E> register(E element) {
		Assert.requiredArgument(element != null, "element");
		ElementRegistration<E> elementRegistration = new ElementRegistration<E>(element, Registration.EMPTY);
		elementRegistration.setEqualsAndHashCode(UUID.randomUUID().toString());
		register(elementRegistration);
		return elementRegistration;
	}

	protected Collection<ElementRegistration<E>> createRegistrations() {
		return new ArrayList<>(8);
	}

	public int getSize() {
		Lock readLock = getReadWriteLock().readLock();
		readLock.lock();
		try {
			int size = registrations == null ? 0 : registrations.size();
			if (getFirstService() != null) {
				size++;
			}

			if (getLastService() != null) {
				size++;
			}
			return size;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public void reload() {
		Lock writeLock = getReadWriteLock().writeLock();
		writeLock.lock();
		try {
			if (registrations != null) {
				publishBatchEvent(loadServices().map((e) -> new RegistryEvent<>(this, ChangeType.UPDATE, e)));
			}
		} finally {
			writeLock.unlock();
		}
	}

	private void unregister(ElementRegistration<E> registration, ChangeType eventType) {
		boolean change = false;
		Lock writeLock = getReadWriteLock().writeLock();
		writeLock.lock();
		try {
			if (eventType == ChangeType.DELETE) {
				change = this.registrations.remove(registration);
			} else if (eventType == ChangeType.CREATE) {
				change = this.registrations.add(registration);
			}
		} finally {
			writeLock.unlock();

			if (change) {
				publishEvent(new RegistryEvent<>(this, eventType, registration.getElement()));
			}
		}
	}
}
