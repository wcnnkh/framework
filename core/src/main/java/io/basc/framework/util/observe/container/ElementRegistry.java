package io.basc.framework.util.observe.container;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.observe.EmptyRegistrations;
import io.basc.framework.util.observe.Publisher;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.RegistrationException;
import io.basc.framework.util.observe.Registrations;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.event.ChangeType;
import io.basc.framework.util.observe.register.BrowseableRegistry;
import io.basc.framework.util.observe.register.PayloadRegistration;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
public class ElementRegistry<E, C extends Collection<ElementRegistration<E>>> extends LazyContainer<C>
		implements BrowseableRegistry<E, ElementRegistration<E>> {
	@RequiredArgsConstructor
	private class BatchRegistrations implements Registrations<ElementRegistration<E>> {
		private final Elements<UpdateableElementRegistration> registrations;

		@Override
		public boolean cancel() {
			Elements<UpdateableElementRegistration> elements = this.registrations.filter((e) -> !e.isCancelled());
			// 全部设置为无效，防止调用默认的事件
			elements.forEach((e) -> e.getLimiter().limited());
			batchDeregister(elements);
			return true;
		}

		@Override
		public Elements<ElementRegistration<E>> getElements() {
			return registrations.map((e) -> e);
		}
	}

	private class UpdateableElementRegistration extends StandardElementRegistrationWrappe<E, ElementRegistration<E>> {
		private UpdateableElementRegistration(
				StandardElementRegistrationWrappe<E, ElementRegistration<E>> combinableServiceRegistration) {
			super(combinableServiceRegistration);
		}

		public UpdateableElementRegistration(ElementRegistration<E> source) {
			super(source, Elements.empty());
		}

		@Override
		public UpdateableElementRegistration and(@NonNull Registration registration) {
			return new UpdateableElementRegistration(super.and(registration));
		}

		@Override
		public boolean cancel(BooleanSupplier cancel) {
			return super.cancel(() -> {
				cleanup();
				changeEventsPublisher.publish(Elements.singleton(new ChangeEvent<E>(getPayload(), ChangeType.DELETE)));
				return true;
			});
		}

		@Override
		public E setPayload(E payload) {
			E oldValue = super.setPayload(payload);
			changeEventsPublisher.publish(Elements.singleton(new ChangeEvent<>(oldValue, payload)));
			return oldValue;
		}
	}

	private final Publisher<? super Elements<ChangeEvent<E>>> changeEventsPublisher;

	public ElementRegistry(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull Publisher<? super Elements<ChangeEvent<E>>> changeEventsPublisher) {
		super(containerSupplier);
		this.changeEventsPublisher = changeEventsPublisher;
	}

	/**
	 * 清理注册表
	 */
	public void cleanup() {
		execute((members) -> {
			Iterator<ElementRegistration<E>> iterator = members.iterator();
			while (iterator.hasNext()) {
				ElementRegistration<E> registration = iterator.next();
				if (registration.isCancelled()) {
					iterator.remove();
				}
			}
			return true;
		});
	}

	protected final boolean batchDeregister(Elements<? extends ElementRegistration<E>> registrations) {
		if (registrations.isEmpty()) {
			return false;
		}

		registrations.forEach(Registration::cancel);
		cleanup();
		Elements<ChangeEvent<E>> events = registrations
				.map((e) -> new ChangeEvent<>(e.getPayload(), ChangeType.DELETE));
		changeEventsPublisher.publish(events);
		return true;
	}

	@Override
	public final void deregister(E element) {
		Elements<ElementRegistration<E>> registrations = read((collection) -> {
			if (collection == null) {
				return Elements.empty();
			}

			return Elements.of(collection).filter((e) -> ObjectUtils.equals(e.getPayload(), element)).toList();
		});
		batchDeregister(registrations);
	}

	public final ElementRegistration<E> getRegistration(Function<? super C, ? extends ElementRegistration<E>> reader) {
		ElementRegistration<E> elementRegistration = read((collection) -> reader.apply(collection));
		if (elementRegistration == null) {
			return null;
		}
		return new UpdateableElementRegistration(elementRegistration);
	}

	@Override
	public final Registrations<ElementRegistration<E>> getRegistrations() {
		return getRegistrations(
				(collection) -> collection == null ? Elements.empty() : Elements.of(collection).toList());
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

	protected ElementRegistration<E> newElementRegistration(E element) {
		return new AtomicElementRegistration<>(element);
	}

	@Override
	public Registrations<ElementRegistration<E>> registers(Iterable<? extends E> elements)
			throws RegistrationException {
		Elements<ElementRegistration<E>> es = Elements.of(elements).map(this::newElementRegistration);
		return writeRegistrations((collection) -> {
			for (ElementRegistration<E> registration : es) {
				if (!collection.add(registration)) {
					registration.cancel();
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

	public final E getPayload(Function<? super C, ? extends PayloadRegistration<E>> getter) {
		return read((container) -> {
			if (container == null) {
				return null;
			}

			PayloadRegistration<E> registration = getter.apply(container);
			return registration == null ? null : registration.getPayload();
		});
	}

	private final Registrations<ElementRegistration<E>> writeRegistrations(
			Function<? super C, ? extends Elements<ElementRegistration<E>>> writer) {
		Elements<ElementRegistration<E>> registrations = write(writer).filter((e) -> !e.isCancelled()).toList();
		Elements<ChangeEvent<E>> events = registrations
				.map((e) -> new ChangeEvent<>(e.getPayload(), ChangeType.CREATE));
		changeEventsPublisher.publish(events);
		return getRegistrations((collection) -> registrations);
	}

	@Override
	public final ElementRegistration<E> register(E element) throws RegistrationException {
		return registers(Arrays.asList(element)).getElements().getUnique();
	}

	public boolean isEmpty() {
		return readAsBoolean((collection) -> collection == null ? true : collection.isEmpty());
	}

	public void clear() {
		getRegistrations().cancel();
	}
}
