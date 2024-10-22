package io.basc.framework.util.collect;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import io.basc.framework.util.Wrapper;

public interface IterableWrapper<E, W extends Iterable<E>> extends Iterable<E>, Wrapper<W> {
	@Override
	default Iterator<E> iterator() {
		return getSource().iterator();
	}

	@Override
	default void forEach(Consumer<? super E> action) {
		getSource().forEach(action);
	}

	@Override
	default Spliterator<E> spliterator() {
		return getSource().spliterator();
	}
}