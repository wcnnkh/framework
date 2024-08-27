package io.basc.framework.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.basc.framework.util.collect.ListWrapper;

public interface ListElementsWrapper<E, W extends List<E>> extends ListWrapper<E, W>, CollectionElementsWrapper<E, W> {

	@Override
	default ListElementsWrapper<E, W> toList() {
		return this;
	}

	@Override
	default void forEach(Consumer<? super E> action) {
		ListWrapper.super.forEach(action);
	}

	@Override
	default Iterator<E> iterator() {
		return ListWrapper.super.iterator();
	}

	@Override
	default boolean isEmpty() {
		return ListWrapper.super.isEmpty();
	}

	@Override
	default Stream<E> stream() {
		return ListWrapper.super.stream();
	}

	@Override
	default boolean contains(Object o) {
		return ListWrapper.super.contains(o);
	}

	@Override
	default Object[] toArray() {
		return ListWrapper.super.toArray();
	}

	@Override
	default <T> T[] toArray(T[] array) {
		return CollectionElementsWrapper.super.toArray(array);
	}

	@Override
	default E get(long index) throws IndexOutOfBoundsException {
		if (index > Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException("index out of range: " + index);
		}

		List<E> list = getSource();
		return list.get((int) index);
	}

	@Override
	default E getUnique() throws NoSuchElementException, NoUniqueElementException {
		List<E> list = getSource();
		if (list.isEmpty()) {
			throw new NoSuchElementException();
		}

		if (list.size() != 1) {
			throw new NoUniqueElementException();
		}
		return list.get(0);
	}

	@Override
	default Elements<E> reverse() {
		return Elements.of(() -> CollectionUtils.getIterator(getSource(), true));
	}

}
