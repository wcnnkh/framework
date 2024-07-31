package io.basc.framework.util.register.container;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.BatchRegistration;
import io.basc.framework.util.register.PayloadRegistration;

public class ElementRegistry<E, C extends Collection<ElementRegistration<E>>>
		extends AbstractRegistry<E, C, ElementRegistration<E>> implements Collection<E> {

	public ElementRegistry(Supplier<? extends C> containerSupplier) {
		super(containerSupplier);
	}

	@Override
	public final boolean add(E e) {
		return addAll(Arrays.asList(e));
	}

	@Override
	public final boolean addAll(Collection<? extends E> elements) {
		Assert.requiredArgument(elements != null, "elements");
		BatchRegistration<ElementRegistration<E>> batchRegistration = registers(Elements.of(elements));
		return !batchRegistration.isInvalid();
	}

	@Override
	protected BatchRegistration<ElementRegistration<E>> batch(
			BatchRegistration<ElementRegistration<E>> batchRegistration) {
		return batchRegistration.batch((es) -> () -> cleanup());
	}

	/**
	 * 清理注册表
	 */
	public void cleanup() {
		execute((members) -> {
			Iterator<ElementRegistration<E>> iterator = members.iterator();
			while (iterator.hasNext()) {
				PayloadRegistration<E> registration = iterator.next();
				if (registration.isInvalid()) {
					iterator.remove();
				}
			}
			return true;
		});
	}

	@Override
	public final void clear() {
		deregister();
	}

	public final boolean contains(Object element) {
		return test((collection) -> collection.contains(element));
	}

	@Override
	public final boolean containsAll(Collection<?> c) {
		return test((members) -> {
			if (members == null) {
				return false;
			}

			// registrations已经被重写，此处可以使用contains，也提高了使用Set时的性能
			return c.stream().allMatch((e) -> members.contains(e));
		});
	}

	@Override
	protected BatchRegistration<ElementRegistration<E>> createBatchRegistration(Iterable<? extends E> items) {
		return new ContainerBatchRegistration<>(Elements.of(items).map(ElementRegistration::new), (a, b) -> a.and(b));
	}

	@Override
	public Elements<ElementRegistration<E>> getRegistrations() {
		return read((collection) -> {
			if (collection == null) {
				return Elements.empty();
			}

			return Elements.of(collection.stream().filter((e) -> !e.isInvalid()).collect(Collectors.toList()));
		});
	}

	@Override
	public final boolean isEmpty() {
		return test((members) -> members == null || members.isEmpty());
	}

	@Override
	public final Iterator<E> iterator() {
		return readAll().iterator();
	}

	/**
	 * 复制为一个list保证线程安全
	 * 
	 * @return
	 */
	public final List<E> readAll() {
		return read((collection) -> {
			if (collection == null) {
				return Collections.emptyList();
			}
			return collection.stream().filter((e) -> !e.isInvalid()).map((e) -> e.getPayload())
					.collect(Collectors.toList());
		});
	}

	@Override
	protected boolean register(C container, ElementRegistration<E> registration) {
		return container.add(registration);
	}

	@Override
	public final boolean remove(Object o) {
		return removeAll(Arrays.asList(o));
	}

	@Override
	public final boolean removeAll(Collection<?> c) {
		return execute((members) -> {
			boolean find = false;
			Iterator<ElementRegistration<E>> iterator = members.iterator();
			while (iterator.hasNext()) {
				ElementRegistration<E> registration = iterator.next();
				if (c.contains(registration.getPayload())) {
					iterator.remove();
					registration.deregister();
					find = true;
				}
			}
			return find;
		});
	}

	@Override
	public final boolean retainAll(Collection<?> c) {
		return test((members) -> {
			if (members == null) {
				return false;
			}

			// ElementRegistration 已经被重写，此处可以使用contains，也提高了使用Set时的性能
			return c.stream().anyMatch((e) -> members.contains(e));
		});
	}

	@Override
	public final int size() {
		return readInt((elements) -> elements == null ? 0 : elements.size());
	}

	@Override
	public final Object[] toArray() {
		return readAll().toArray();
	}

	@Override
	public final <T> T[] toArray(T[] a) {
		return readAll().toArray(a);
	}
}
