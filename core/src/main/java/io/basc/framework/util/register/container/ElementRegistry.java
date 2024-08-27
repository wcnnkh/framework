package io.basc.framework.util.register.container;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.observe.ChangeType;
import io.basc.framework.util.register.BrowseableRegistry;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.Registrations;
import io.basc.framework.util.register.empty.EmptyRegistrations;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class ElementRegistry<E, C extends Collection<ElementRegistration<E>>> extends LazyContainer<C>
		implements BrowseableRegistry<E, ElementRegistration<E>>, Collection<E> {
	@RequiredArgsConstructor
	private class BatchRegistrations implements Registrations<ElementRegistration<E>> {
		private final Elements<UpdateableElementRegistration> registrations;

		@Override
		public void deregister() throws RegistrationException {
			Elements<UpdateableElementRegistration> elements = this.registrations.filter((e) -> !e.isInvalid());
			// 全部设置为无效，防止调用默认的事件
			elements.forEach((e) -> e.getLimiter().limited());
			batchDeregister(elements);
		}

		@Override
		public Elements<ElementRegistration<E>> getRegistrations() {
			return registrations.map((e) -> e);
		}
	}

	private class UpdateableElementRegistration extends CombinableElementRegistration<E, ElementRegistration<E>> {
		private UpdateableElementRegistration(
				CombinableElementRegistration<E, ElementRegistration<E>> combinableServiceRegistration) {
			super(combinableServiceRegistration);
		}

		public UpdateableElementRegistration(ElementRegistration<E> source) {
			super(source, Registration.EMPTY);
		}

		@Override
		public UpdateableElementRegistration and(@NonNull Registration registration) {
			return new UpdateableElementRegistration(super.and(registration));
		}

		@Override
		public void deregister(Runnable runnable) throws RegistrationException {
			super.deregister(() -> {
				try {
					runnable.run();
				} finally {
					cleanup();
					eventPublishService.publishEvent(new ChangeEvent<E>(getPayload(), ChangeType.DELETE));
				}
			});
		}

		@Override
		public E setValue(E value) {
			E oldValue = super.setValue(value);
			eventPublishService.publishEvent(new ChangeEvent<>(value, oldValue));
			return oldValue;
		}
	}

	private final EventPublishService<ChangeEvent<E>> eventPublishService;

	public ElementRegistry(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull EventPublishService<ChangeEvent<E>> eventPublishService) {
		super(containerSupplier);
		this.eventPublishService = eventPublishService;
	}

	/**
	 * 清理注册表
	 */
	public void cleanup() {
		execute((members) -> {
			Iterator<ElementRegistration<E>> iterator = members.iterator();
			while (iterator.hasNext()) {
				ElementRegistration<E> registration = iterator.next();
				if (registration.isInvalid()) {
					iterator.remove();
				}
			}
			return true;
		});
	}

	@Override
	public final boolean add(E e) {
		Registration registration = register(e);
		return !registration.isInvalid();
	}

	@Override
	public final boolean addAll(Collection<? extends E> c) {
		Registrations<ElementRegistration<E>> registrations = registers(c);
		return !registrations.getRegistrations().isEmpty();
	}

	private boolean batchDeregister(Elements<? extends ElementRegistration<E>> registrations) {
		if (registrations.isEmpty()) {
			return false;
		}

		registrations.forEach(Registration::deregister);
		cleanup();
		Elements<ChangeEvent<E>> events = registrations
				.map((e) -> new ChangeEvent<>(e.getPayload(), ChangeType.DELETE));
		eventPublishService.publishBatchEvents(events);
		return true;
	}

	@Override
	public final void clear() {
		deregister();
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
	public final void deregister() throws RegistrationException {
		Elements<ElementRegistration<E>> registrations = read((collection) -> {
			if (collection == null) {
				return Elements.empty();
			}

			return Elements.of(collection).toList();
		});
		batchDeregister(registrations);
	}

	@Override
	public final void deregister(E element) {
		remove(element);
	}

	public final ElementRegistration<E> getRegistration(Function<? super C, ? extends ElementRegistration<E>> reader) {
		ElementRegistration<E> elementRegistration = read((collection) -> reader.apply(collection));
		if (elementRegistration == null) {
			return null;
		}
		return new UpdateableElementRegistration(elementRegistration);
	}

	@Override
	public final Elements<ElementRegistration<E>> getRegistrations() {
		return getRegistrations(
				(collection) -> collection == null ? Elements.empty() : Elements.of(collection).toList())
				.getRegistrations();
	}

	public final Registrations<ElementRegistration<E>> getRegistrations(
			Function<? super C, ? extends Elements<ElementRegistration<E>>> reader) {
		Elements<ElementRegistration<E>> registrations = read((collection) -> reader.apply(collection));
		if (registrations == null || registrations.isEmpty()) {
			return EmptyRegistrations.empty();
		}

		Elements<UpdateableElementRegistration> updateableRegistrations = registrations
				.map((e) -> new UpdateableElementRegistration(e));
		return new BatchRegistrations(updateableRegistrations);
	}

	@Override
	public final boolean isEmpty() {
		return readAsBoolean((collection) -> collection == null ? true : collection.isEmpty());
	}

	protected ElementRegistration<E> newElementRegistration(E element) {
		return new AtomicElementRegistration<>(element);
	}

	@Override
	public final Registrations<ElementRegistration<E>> registers(Iterable<? extends E> elements)
			throws RegistrationException {
		Elements<ElementRegistration<E>> es = Elements.of(elements).map(this::newElementRegistration);
		return writeRegistrations((collection) -> {
			for (ElementRegistration<E> registration : es) {
				if (!collection.add(registration)) {
					registration.deregister();
				}
			}
			return es.toList();
		});
	}

	public final Registrations<ElementRegistration<E>> registers(Iterable<? extends E> elements,
			BiConsumer<? super C, ? super Elements<ElementRegistration<E>>> register) throws RegistrationException {
		Elements<ElementRegistration<E>> es = Elements.of(elements).map(this::newElementRegistration);
		return writeRegistrations((collection) -> {
			register.accept(collection, es);
			return es.toList();
		});
	}

	@Override
	public void reload() {
		cleanup();
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

	private final Registrations<ElementRegistration<E>> writeRegistrations(
			Function<? super C, ? extends Elements<ElementRegistration<E>>> writer) {
		Elements<ElementRegistration<E>> registrations = write(writer).filter((e) -> !e.isInvalid()).toList();
		Elements<ChangeEvent<E>> events = registrations
				.map((e) -> new ChangeEvent<>(e.getPayload(), ChangeType.CREATE));
		eventPublishService.publishBatchEvents(events);
		return getRegistrations((collection) -> registrations);
	}

	@Override
	public Iterator<E> iterator() {
		return read((collection) -> collection == null ? Collections.emptyIterator()
				: collection.stream().filter((e) -> !e.isInvalid()).map((e) -> e.getPayload())
						.collect(Collectors.toList()).iterator());
	}

	@Override
	public final Stream<E> stream() {
		return BrowseableRegistry.super.stream();
	}

	@Override
	public final Object[] toArray() {
		return BrowseableRegistry.super.toArray();
	}
}
