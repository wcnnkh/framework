package io.basc.framework.observe.register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;
import io.basc.framework.util.RegistrationException;
import io.basc.framework.util.Registrations;
import io.basc.framework.util.VersionRegistration;
import io.basc.framework.util.Wrapper;
import io.basc.framework.util.element.Elements;

public class ElementRegistry<E> extends AbstractElementRegistry<E> {
	private volatile long version;
	private volatile List<Wrapper<E>> wrappers;

	@Override
	public Registrations<ElementRegistration<E>> clear() throws RegistrationException {
		Lock writeLock = getReadWriteLock().writeLock();
		writeLock.lock();
		try {
			List<Wrapper<E>> elements = wrappers == null ? Collections.emptyList() : new ArrayList<>(wrappers);
			wrappers = null;
			this.version++;
			List<ElementRegistration<E>> registrations = elements.stream().map((e) -> {
				VersionRegistration versionRegistration = new VersionRegistration(() -> this.version,
						() -> unregister(e, RegistryEventType.REGISTER));
				return new ElementRegistration<E>(e.getDelegateSource(), versionRegistration);
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
		return Elements.of(wrappers.stream().map((e) -> e.getDelegateSource()).collect(Collectors.toList()));
	}

	public ElementRegistration<E> register(E element) {
		Assert.requiredArgument(element != null, "element");
		Lock writeLock = getReadWriteLock().writeLock();
		writeLock.lock();
		try {
			if (wrappers == null) {
				wrappers = createWrapperList();
			}

			Wrapper<E> wrapper = new Wrapper<E>(element);
			wrapper.setEqualsAndHashCode(UUID.randomUUID().toString());
			wrappers.add(wrapper);
			Registration registration = new VersionRegistration(() -> this.version,
					() -> unregister(wrapper, RegistryEventType.UNREGISTER));
			return new ElementRegistration<E>(element, registration);
		} finally {
			writeLock.unlock();
			publishEvent(new RegistryEvent<>(this, RegistryEventType.REGISTER, element));
		}
	}

	protected List<Wrapper<E>> createWrapperList() {
		return new ArrayList<>(8);
	}

	public int getSize() {
		Lock readLock = getReadWriteLock().readLock();
		readLock.lock();
		try {
			int size = wrappers == null ? 0 : wrappers.size();
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
		if (wrappers != null) {
			publishBatchEvent(loadServices().map((e) -> new RegistryEvent<>(this, RegistryEventType.UPDATE, e)));
		}
	}

	private void unregister(Wrapper<E> element, RegistryEventType eventType) {
		Lock writeLock = getReadWriteLock().writeLock();
		writeLock.lock();
		try {
			if (eventType == RegistryEventType.UNREGISTER) {
				this.wrappers.remove(element);
			} else if (eventType == RegistryEventType.REGISTER) {
				this.wrappers.add(element);
			}
		} finally {
			writeLock.unlock();
			publishEvent(new RegistryEvent<>(this, eventType, element.getDelegateSource()));
		}
	}
}
