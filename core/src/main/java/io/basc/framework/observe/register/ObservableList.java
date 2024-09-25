package io.basc.framework.observe.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.ListElements;
import io.basc.framework.util.actor.ChangeType;
import io.basc.framework.util.register.PayloadBatchRegistration;
import io.basc.framework.util.register.PayloadRegistration;
import lombok.NonNull;

public class ObservableList<E> extends ObservableCollection<E, List<PayloadRegistration<E>>> implements List<E> {

	public ObservableList() {
		this(ArrayList::new);
	}

	public ObservableList(@NonNull Supplier<? extends List<PayloadRegistration<E>>> listSupplier) {
		super(listSupplier);
	}

	@Override
	public final boolean addAll(int index, Collection<? extends E> c) {
		Elements<PayloadRegistration<E>> regs = write((list) -> {
			PayloadBatchRegistration<E> batchRegistration = new PayloadBatchRegistration<>(c);
			ListElements<PayloadRegistration<E>> elements = batchRegistration.getServices().toList();
			if (list.addAll(index, elements)) {
				return elements;
			}
			return Elements.empty();
		});

		if (regs.isEmpty()) {
			return false;
		}

		publishBatchEvent(regs, ChangeType.CREATE);
		return true;
	}

	@Override
	public final E get(int index) {
		return read((list) -> {
			PayloadRegistration<E> elementRegistration = list.get(index);
			if (elementRegistration == null) {
				return null;
			}

			return elementRegistration.isInvalid() ? null : elementRegistration.getPayload();
		});
	}

	@Override
	public final E set(int index, E element) {
		PayloadRegistration<E> registration = new PayloadRegistration<E>(element);
		PayloadRegistration<E> old = write((list) -> {
			return list.set(index, registration);
		});

		publishEvent(new RegistryEvent<>(this, ChangeType.CREATE, element));
		if (old == null) {
			return null;
		}
		old.unregister();
		return old.getPayload();
	}

	@Override
	public final void add(int index, E element) {
		addAll(index, Arrays.asList(element));
	}

	@Override
	public final E remove(int index) {
		PayloadRegistration<E> registration = write((list) -> {
			return list.remove(index);
		});
		if (registration == null) {
			return null;
		}
		registration.unregister();
		return registration.getPayload();
	}

	@Override
	public final int indexOf(Object o) {
		return readInt((list) -> {
			if (list == null) {
				return -1;
			}

			return list.indexOf(o);
		});
	}

	@Override
	public final int lastIndexOf(Object o) {
		return readInt((list) -> {
			if (list == null) {
				return -1;
			}

			return list.lastIndexOf(o);
		});
	}

	@Override
	public final ListIterator<E> listIterator() {
		return read((list) -> {
			if (list == null) {
				return Collections.emptyListIterator();
			}

			List<E> newList = list.stream().map((e) -> e.getPayload()).collect(Collectors.toList());
			return newList.listIterator();
		});
	}

	@Override
	public final ListIterator<E> listIterator(int index) {
		return read((list) -> {
			if (list == null) {
				return Collections.emptyListIterator();
			}

			List<E> newList = list.stream().map((e) -> e.getPayload()).collect(Collectors.toList());
			return newList.listIterator(index);
		});
	}

	/**
	 * 回调参数不会为空
	 */
	@Override
	public boolean test(Predicate<? super List<PayloadRegistration<E>>> predicate) {
		return super.test((list) -> predicate.test(list == null ? Collections.emptyList() : list));
	}

	/**
	 * 回调参数不会为空
	 */
	@Override
	public <R> R read(Function<? super List<PayloadRegistration<E>>, ? extends R> reader) {
		return super.read((list) -> reader.apply(list == null ? Collections.emptyList() : list));
	}

	/**
	 * 回调参数不会为空
	 */
	@Override
	public int readInt(ToIntFunction<? super List<PayloadRegistration<E>>> reader) {
		return super.readInt((list) -> reader.applyAsInt(list == null ? Collections.emptyList() : list));
	}

	@Override
	public final List<E> subList(int fromIndex, int toIndex) {
		return read((list) -> {
			if (list == null) {
				return Collections.emptyList();
			}

			List<E> newList = list.stream().map((e) -> e.getPayload()).collect(Collectors.toList());
			return newList.subList(fromIndex, toIndex);
		});
	}
}
