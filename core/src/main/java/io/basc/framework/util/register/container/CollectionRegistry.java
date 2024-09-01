package io.basc.framework.util.register.container;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.Registrations;
import lombok.NonNull;

public class CollectionRegistry<E, C extends Collection<ElementRegistration<E>>> extends ElementRegistry<E, C>
		implements Collection<E> {

	public CollectionRegistry(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull EventPublishService<ChangeEvent<E>> eventPublishService) {
		super(containerSupplier, eventPublishService);
	}

	@Override
	public final boolean add(E e) {
		Registration registration = register(e);
		return !registration.isInvalid();
	}

	@Override
	public final boolean addAll(Collection<? extends E> c) {
		Registrations<ElementRegistration<E>> registrations = registers(c);
		return !registrations.getElements().isEmpty();
	}

	@Override
	public final void clear() {
		getRegistrations().deregister();
	}

	@Override
	public final boolean contains(Object o) {
		return readAsBoolean((collection) -> collection.contains(o));
	}

	@Override
	public final boolean containsAll(Collection<?> c) {
		return readAsBoolean((collection) -> collection == null ? false : collection.containsAll(c));
	}

	@Override
	public final boolean isEmpty() {
		return readAsBoolean((collection) -> collection == null ? true : collection.isEmpty());
	}

	@Override
	public final boolean remove(Object o) {
		Elements<ElementRegistration<E>> registrations = read((collection) -> {
			if (collection == null) {
				return Elements.empty();
			}

			return Elements.of(collection).filter((e) -> ObjectUtils.equals(e.getPayload(), o)).toList();
		});

		return batchDeregister(registrations);
	}

	@Override
	public final boolean removeAll(Collection<?> c) {
		Elements<ElementRegistration<E>> registrations = read((collection) -> {
			if (collection == null) {
				return Elements.empty();
			}

			return Elements.of(collection).filter((e) -> c.contains(e.getPayload())).toList();
		});
		return batchDeregister(registrations);
	}

	@Override
	public final boolean retainAll(Collection<?> c) {
		return readAsBoolean((collection) -> collection == null ? false : collection.retainAll(c));
	}

	@Override
	public final int size() {
		return readInt((collection) -> collection == null ? 0 : collection.size());
	}

	@Override
	public Iterator<E> iterator() {
		return read((collection) -> collection == null ? Collections.emptyIterator()
				: collection.stream().filter((e) -> !e.isInvalid()).map((e) -> e.getPayload())
						.collect(Collectors.toList()).iterator());
	}

	@Override
	public final Stream<E> stream() {
		return Collection.super.stream();
	}

	@Override
	public final Object[] toArray() {
		return getElements().toArray();
	}

	@Override
	public final <T> T[] toArray(T[] a) {
		return getElements().toArray(a);
	}
}
