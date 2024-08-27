package io.basc.framework.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.util.collect.CollectionWrapper;

public interface CollectionElementsWrapper<E, W extends Collection<E>>
		extends CollectionWrapper<E, W>, IterableElementsWrapper<E, W> {

	@Override
	default boolean isUnique() {
		return size() == 1;
	}

	@Override
	default Iterator<E> iterator() {
		return CollectionWrapper.super.iterator();
	}

	@Override
	default boolean isEmpty() {
		return CollectionWrapper.super.isEmpty();
	}

	@Override
	default Stream<E> stream() {
		return CollectionWrapper.super.stream();
	}

	@Override
	default boolean contains(Object element) {
		return CollectionWrapper.super.contains(element);
	}

	@Override
	default Object[] toArray() {
		return CollectionWrapper.super.toArray();
	}

	@Override
	default <T> T[] toArray(T[] array) {
		return CollectionWrapper.super.toArray(array);
	}

}
