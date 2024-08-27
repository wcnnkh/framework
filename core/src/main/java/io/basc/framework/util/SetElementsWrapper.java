package io.basc.framework.util;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.basc.framework.util.collect.SetWrapper;

public interface SetElementsWrapper<E, W extends Set<E>> extends SetWrapper<E, W>, CollectionElementsWrapper<E, W> {

	@Override
	default void forEach(Consumer<? super E> action) {
		SetWrapper.super.forEach(action);
	}

	@Override
	default Iterator<E> iterator() {
		return SetWrapper.super.iterator();
	}

	@Override
	default boolean isEmpty() {
		return SetWrapper.super.isEmpty();
	}

	@Override
	default Stream<E> stream() {
		return SetWrapper.super.stream();
	}

	@Override
	default boolean contains(Object o) {
		return SetWrapper.super.contains(o);
	}

	@Override
	default Object[] toArray() {
		return SetWrapper.super.toArray();
	}

	@Override
	default <T> T[] toArray(T[] a) {
		return SetWrapper.super.toArray(a);
	}

	@Override
	default Elements<E> distinct() {
		return this;
	}

	@Override
	default SetElementsWrapper<E, W> toSet() {
		return this;
	}
}
