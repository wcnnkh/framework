package io.basc.framework.util;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.basc.framework.util.collect.IterableWrapper;

public interface IterableElementsWrapper<E, W extends Iterable<E>> extends Elements<E>, IterableWrapper<E, W> {

	@Override
	default void forEach(Consumer<? super E> action) {
		Elements.super.forEach(action);
	}

	@Override
	default Iterator<E> iterator() {
		return IterableWrapper.super.iterator();
	}

	@Override
	default Stream<E> stream() {
		return Streams.stream(spliterator());
	}
}
