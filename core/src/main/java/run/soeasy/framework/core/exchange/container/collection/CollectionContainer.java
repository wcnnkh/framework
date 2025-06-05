package run.soeasy.framework.core.exchange.container.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.EmptyRegistrations;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.Registrations;
import run.soeasy.framework.core.exchange.container.AbstractContainer;
import run.soeasy.framework.core.exchange.container.AtomicElementRegistration;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.exchange.container.PayloadRegistration;
import run.soeasy.framework.core.exchange.container.RegistrationException;
import run.soeasy.framework.core.exchange.container.StandardElementRegistrationWrappe;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;
import run.soeasy.framework.core.function.ThrowingSupplier;

public class CollectionContainer<E, C extends Collection<ElementRegistration<E>>>
		extends AbstractContainer<C, E, ElementRegistration<E>> implements Collection<E> {
	@RequiredArgsConstructor
	private class BatchRegistrations implements Registrations<ElementRegistration<E>> {
		private final Elements<UpdateableElementRegistration> registrations;

		@Override
		public boolean cancel() {
			Elements<UpdateableElementRegistration> elements = this.registrations.filter((e) -> !e.isCancelled());
			// 全部设置为无效，防止调用默认的事件
			elements.forEach((e) -> e.getLimiter().limited());
			batchDeregister(elements, publisher);
			return true;
		}

		@Override
		public Elements<ElementRegistration<E>> getElements() {
			return registrations.map((e) -> e);
		}
	}

	private class UpdateableElementRegistration extends StandardElementRegistrationWrappe<E, ElementRegistration<E>> {
		public UpdateableElementRegistration(ElementRegistration<E> source) {
			super(source, Elements.empty());
		}

		private UpdateableElementRegistration(
				StandardElementRegistrationWrappe<E, ElementRegistration<E>> combinableServiceRegistration) {
			super(combinableServiceRegistration);
		}

		@Override
		public UpdateableElementRegistration and(@NonNull Registration registration) {
			return new UpdateableElementRegistration(super.and(registration));
		}

		@Override
		public boolean cancel(BooleanSupplier cancel) {
			return super.cancel(() -> {
				cleanup();
				publisher.publish(Elements.singleton(new ChangeEvent<E>(getPayload(), ChangeType.DELETE)));
				return true;
			});
		}

		@Override
		public E setPayload(E payload) {
			E oldValue = super.setPayload(payload);
			publisher.publish(Elements.singleton(new ChangeEvent<>(oldValue, payload)));
			return oldValue;
		}
	}

	private volatile Publisher<? super Elements<ChangeEvent<E>>> publisher = Publisher.ignore();

	public CollectionContainer(@NonNull ThrowingSupplier<? extends C, ? extends RuntimeException> containerSource) {
		super(containerSource);
	}

	@Override
	public final boolean add(E e) {
		Registration registration = register(e);
		return !registration.isCancelled();
	}

	@Override
	public final boolean addAll(Collection<? extends E> c) {
		Registrations<ElementRegistration<E>> registrations = batchRegister(Elements.of(c), getPublisher());
		return !registrations.getElements().isEmpty();
	}

	protected final Receipt batchDeregister(Elements<? extends ElementRegistration<E>> registrations,
			Publisher<? super Elements<ChangeEvent<E>>> publisher) {
		if (registrations.isEmpty()) {
			return Receipt.FAILURE;
		}

		registrations.forEach(Registration::cancel);
		cleanup();
		Elements<ChangeEvent<E>> events = registrations
				.map((e) -> new ChangeEvent<>(e.getPayload(), ChangeType.DELETE));
		return publisher.publish(events);
	}

	public final Registrations<ElementRegistration<E>> batchRegister(Elements<? extends E> elements)
			throws RegistrationException {
		return batchRegister(elements, this.publisher);
	}

	public Registrations<ElementRegistration<E>> batchRegister(Elements<? extends E> elements,
			Publisher<? super Elements<ChangeEvent<E>>> publisher) throws RegistrationException {
		return writeRegistrations((collection) -> {
			Elements<ElementRegistration<E>> es = elements.map(this::newElementRegistration);
			for (ElementRegistration<E> registration : es) {
				if (!collection.add(registration)) {
					registration.cancel();
				}
			}
			// 这里需要调用一下toList来隐性执行一下add行为
			return es.toList();
		}, publisher);
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

	public void clear() {
		getRegistrations().cancel();
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
	public Receipt deregisters(@NonNull Elements<? extends E> services) {
		return deregisters(services, publisher);
	}

	public Receipt deregisters(Elements<? extends E> services, Publisher<? super Elements<ChangeEvent<E>>> publisher) {
		Collection<? extends E> removes = services.toSet();
		Elements<ElementRegistration<E>> registrations = readAsElements((collection) -> {
			if (collection == null) {
				return Elements.empty();
			}
			return Elements.of(collection).filter((e) -> removes.contains(e.getPayload()));
		});
		return batchDeregister(registrations, publisher);
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		read((collection) -> {
			if (collection == null) {
				return null;
			}

			collection.forEach((e) -> action.accept(e.getPayload()));
			return null;
		});
	}

	@Override
	public Elements<ElementRegistration<E>> getElements() {
		return getRegistrations().getElements().filter((e) -> !e.isCancelled());
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

	public final ElementRegistration<E> getRegistration(Function<? super C, ? extends ElementRegistration<E>> reader) {
		ElementRegistration<E> elementRegistration = read((collection) -> reader.apply(collection));
		if (elementRegistration == null) {
			return null;
		}
		return new UpdateableElementRegistration(elementRegistration);
	}

	public final Registrations<ElementRegistration<E>> getRegistrations() {
		return getRegistrations(
				(collection) -> collection == null ? Elements.empty() : Elements.of(collection).toList());
	}

	public final Registrations<ElementRegistration<E>> getRegistrations(
			Function<? super C, ? extends Elements<ElementRegistration<E>>> reader) {
		Elements<ElementRegistration<E>> registrations = readAsElements((collection) -> reader.apply(collection));
		if (registrations == null || registrations.isEmpty()) {
			return EmptyRegistrations.empty();
		}

		Elements<UpdateableElementRegistration> updateableRegistrations = registrations
				.map((e) -> new UpdateableElementRegistration(e));
		return new BatchRegistrations(updateableRegistrations);
	}

	public boolean isEmpty() {
		return readAsBoolean((collection) -> collection == null ? true : collection.isEmpty());
	}

	@Override
	public Iterator<E> iterator() {
		return getElements().map((e) -> e.getPayload()).iterator();
	}

	protected AtomicElementRegistration<E> newElementRegistration(E element) {
		return new AtomicElementRegistration<>(element);
	}

	@Override
	public ElementRegistration<E> register(E element) throws RegistrationException {
		return register(element, publisher);
	}

	public ElementRegistration<E> register(E element, Publisher<? super Elements<ChangeEvent<E>>> publisher)
			throws RegistrationException {
		return batchRegister(Elements.singleton(element), publisher).getElements().first();
	}

	@Override
	public Registration registers(@NonNull Elements<? extends E> elements) throws RegistrationException {
		return batchRegister(elements, publisher);
	}

	public final Registrations<ElementRegistration<E>> registers(Iterable<? extends E> elements,
			BiConsumer<? super C, ? super Elements<ElementRegistration<E>>> register,
			Publisher<? super Elements<ChangeEvent<E>>> publisher) throws RegistrationException {
		Elements<ElementRegistration<E>> es = Elements.of(elements).map(this::newElementRegistration);
		return writeRegistrations((collection) -> {
			register.accept(collection, es);
			return es.toList();
		}, publisher);
	}

	@Override
	public final boolean remove(Object o) {
		Elements<ElementRegistration<E>> registrations = readAsElements((collection) -> {
			if (collection == null) {
				return Elements.empty();
			}

			return Elements.of(collection).filter((e) -> ObjectUtils.equals(e.getPayload(), o));
		});

		return batchDeregister(registrations, getPublisher()).isSuccess();
	}

	@Override
	public final boolean removeAll(Collection<?> c) {
		Elements<ElementRegistration<E>> registrations = readAsElements((collection) -> {
			if (collection == null) {
				return Elements.empty();
			}

			return Elements.of(collection).filter((e) -> c.contains(e.getPayload()));
		});
		return batchDeregister(registrations, getPublisher()).isSuccess();
	}

	@Override
	public final boolean retainAll(Collection<?> c) {
		return readAsBoolean((collection) -> collection == null ? false : collection.retainAll(c));
	}

	@Override
	public final int size() {
		return readAsInt((collection) -> collection == null ? 0 : collection.size());
	}

	@Override
	public Stream<E> stream() {
		return getElements().map((e) -> e.getPayload()).stream();
	}

	@Override
	public Object[] toArray() {
		return toList().toArray();
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return super.toArray(array);
	}
	
	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		return super.toArray(generator);
	}

	private final Registrations<ElementRegistration<E>> writeRegistrations(
			Function<? super C, ? extends Elements<ElementRegistration<E>>> writer,
			Publisher<? super Elements<ChangeEvent<E>>> publisher) {
		Elements<ElementRegistration<E>> registrations = write(writer).filter((e) -> !e.isCancelled()).toList();
		registrations.forEach((e) -> e.start());
		Elements<ChangeEvent<E>> events = registrations
				.map((e) -> new ChangeEvent<>(e.getPayload(), ChangeType.CREATE));
		publisher.publish(events);
		return getRegistrations((collection) -> registrations);
	}
}
