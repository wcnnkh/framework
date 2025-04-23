package run.soeasy.framework.core.exchange.container;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.function.ThrowingSupplier;

public class ListContainer<E, C extends List<ElementRegistration<E>>> extends CollectionContainer<E, C>
		implements List<E> {

	public ListContainer(@NonNull ThrowingSupplier<? extends C, ? extends RuntimeException> containerSource) {
		super(containerSource);
	}

	@Override
	public final boolean addAll(int index, Collection<? extends E> c) {
		return !registers(c, (list, elements) -> {
			list.addAll(index, elements.toList());
		}, getPublisher()).getElements().isEmpty();
	}

	@Override
	public final E get(int index) {
		return read((list) -> {
			ElementRegistration<E> registration = list.get(index);
			return registration == null ? null : registration.getPayload();
		});
	}

	public final ElementRegistration<E> getRegistration(int index) {
		return getRegistration((list) -> {
			if (list == null) {
				throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + 0);
			}
			return list.get(index);
		});
	}

	@Override
	public final E set(int index, E element) {
		ElementRegistration<E> elementRegistration = getRegistration(index);
		return elementRegistration.setPayload(element);
	}

	@Override
	public final void add(int index, E element) {
		registers(Elements.singleton(element), (list, elements) -> {
			elements.forEach((e) -> list.add(index, e));
		}, getPublisher());
	}

	@Override
	public final E remove(int index) {
		ElementRegistration<E> elementRegistration = getRegistration(index);
		elementRegistration.cancel();
		return elementRegistration.getPayload();
	}

	@Override
	public int indexOf(Object o) {
		return readAsInt((list) -> list == null ? -1 : list.indexOf(o));
	}

	@Override
	public final int lastIndexOf(Object o) {
		return readAsInt((list) -> list == null ? -1 : list.lastIndexOf(o));
	}

	@Override
	public final ListIterator<E> listIterator() {
		return toList().listIterator();
	}

	@Override
	public final ListIterator<E> listIterator(int index) {
		return toList().listIterator(index);
	}

	@Override
	public final List<E> subList(int fromIndex, int toIndex) {
		return toList().subList(fromIndex, toIndex);
	}

}
