package io.basc.framework.util.observe.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.event.EventDispatcher;
import io.basc.framework.util.event.support.DefaultEventDispatcher;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.register.container.ElementRegistration;
import lombok.NonNull;

public class ObservableList<E> extends ObservableCollection<E, List<ElementRegistration<E>>> implements List<E> {

	public ObservableList() {
		this(ArrayList::new, new DefaultEventDispatcher<>());
	}

	public ObservableList(@NonNull Supplier<? extends List<ElementRegistration<E>>> containerSupplier,
			@NonNull EventDispatcher<ChangeEvent<E>> eventDispatcher) {
		super(containerSupplier, eventDispatcher);
	}

	@Override
	public final boolean addAll(int index, Collection<? extends E> c) {
		return !registers(c, (list, elements) -> {
			list.addAll(index, elements.toList());
		}).getRegistrations().isEmpty();
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
		return elementRegistration.setValue(element);
	}

	@Override
	public final void add(int index, E element) {
		registers(Elements.singleton(element), (list, elements) -> {
			elements.forEach((e) -> list.add(index, e));
		});
	}

	@Override
	public final E remove(int index) {
		ElementRegistration<E> elementRegistration = getRegistration(index);
		elementRegistration.deregister();
		return elementRegistration.getPayload();
	}

	@Override
	public int indexOf(Object o) {
		return readInt((list) -> list == null ? -1 : list.indexOf(o));
	}

	@Override
	public final int lastIndexOf(Object o) {
		return readInt((list) -> list == null ? -1 : list.lastIndexOf(o));
	}

	@Override
	public final ListIterator<E> listIterator() {
		return getServices().toList().listIterator();
	}

	@Override
	public final ListIterator<E> listIterator(int index) {
		return getServices().toList().listIterator(index);
	}

	@Override
	public final List<E> subList(int fromIndex, int toIndex) {
		return getServices().toList().subList(fromIndex, toIndex);
	}
}
