package io.basc.framework.observe.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

import io.basc.framework.observe.PublishService;
import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.register.container.AtomicElementRegistration;
import io.basc.framework.util.observe_old.Observer;
import lombok.NonNull;

public class ObservableList<E> extends ObservableElementRegistry<E, List<AtomicElementRegistration<E>>>
		implements List<E> {

	public ObservableList() {
		this(ArrayList::new);
	}

	public ObservableList(@NonNull Supplier<? extends List<AtomicElementRegistration<E>>> containerSupplier) {
		this(containerSupplier, new Observer<>());
	}

	public ObservableList(@NonNull Supplier<? extends List<AtomicElementRegistration<E>>> containerSupplier,
			@NonNull PublishService<ChangeEvent<E>> publishService) {
		super(containerSupplier, publishService);
	}

	@Override
	public final boolean addAll(int index, Collection<? extends E> c) {
		doRegister(c, (list, rs) -> {
			list.addAll(index, rs.toList());
		});
		return true;
	}

	public final AtomicElementRegistration<E> getRegistration(int index) {
		return read((list) -> list == null ? null : list.get(index));
	}

	@Override
	public final E get(int index) {
		AtomicElementRegistration<E> registration = getRegistration(index);
		return registration == null ? null : registration.getService();
	}

	@Override
	public final E set(int index, E element) {
		AtomicElementRegistration<E> elementRegistration = getRegistration(index);
		if (elementRegistration == null) {
			throw new IndexOutOfBoundsException("" + index);
		}

		return elementRegistration.setValue(element);
	}

	@Override
	public final void add(int index, E element) {
		doRegister(Elements.singleton(element), (list, rs) -> rs.forEach((r) -> list.add(index, r)));
	}

	@Override
	public final E remove(int index) {
		AtomicElementRegistration<E> registration = getRegistration(index);
		if (registration == null) {
			throw new IndexOutOfBoundsException("" + index);
		}
		registration.deregister();
		return registration.getService();
	}

	@Override
	public final int indexOf(Object o) {
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
