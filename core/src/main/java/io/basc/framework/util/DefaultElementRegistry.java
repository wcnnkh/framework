package io.basc.framework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import io.basc.framework.event.ChangeType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(exclude = "lock")
@EqualsAndHashCode(exclude = "lock")
public class DefaultElementRegistry<E> implements ElementRegistry<E> {
	private Object lock = this;
	private final Collection<E> elements;
	private volatile long version;

	public DefaultElementRegistry() {
		this(new CopyOnWriteArraySet<>());
	}

	public DefaultElementRegistry(Collection<E> elements) {
		Assert.requiredArgument(elements != null, "elements");
		this.elements = new ElementCollection<>(elements);
	}

	@Override
	public Registrations<ElementRegistration<E>> clear() throws RegistrationException {
		synchronized (lock) {
			List<E> elements = new ArrayList<>(this.elements);
			this.elements.clear();
			this.version++;

			List<ElementRegistration<E>> registrations = elements.stream().map((e) -> {
				VersionRegistration versionRegistration = new VersionRegistration(() -> this.version,
						() -> unregister(e, ChangeType.CREATE));
				return new ElementRegistration<E>(e, versionRegistration);
			}).collect(Collectors.toList());
			return new Registrations<>(Elements.of(registrations));
		}
	}

	public boolean contains(E element) {
		return elements.contains(element);
	}

	public final Object getLock() {
		return lock;
	}

	public long getVersion() {
		return version;
	}

	public ElementRegistration<E> register(E element) {
		Assert.requiredArgument(element != null, "element");
		synchronized (lock) {
			if (this.elements.add(element)) {
				Registration registration = new VersionRegistration(() -> this.version,
						() -> unregister(element, ChangeType.DELETE));
				return new ElementRegistration<E>(element, registration);
			}
		}
		return ElementRegistration.empty();
	}

	@Override
	public final Registrations<ElementRegistration<E>> registers(Iterable<? extends E> elements)
			throws RegistrationException {
		return ElementRegistry.super.registers(elements);
	}

	public void setLock(Object lock) {
		Assert.requiredArgument(lock != null, "lock");
		this.lock = lock;
	}

	private void unregister(E element, ChangeType changeType) {
		synchronized (lock) {
			if (changeType == ChangeType.DELETE) {
				this.elements.remove(element);
			} else if (changeType == ChangeType.CREATE) {
				this.elements.add(element);
			}
		}
	}

	@Override
	public Elements<E> getElements() {
		return Elements.of(elements);
	}
}
