package io.basc.framework.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface CollectionWrapper<E, W extends Collection<E>> extends Collection<E>, IterableWrapper<E, W> {

	@Override
	default int size() {
		return getSource().size();
	}

	@Override
	default boolean isEmpty() {
		return getSource().isEmpty();
	}

	@Override
	default boolean contains(Object o) {
		return getSource().contains(o);
	}

	@Override
	default Iterator<E> iterator() {
		return getSource().iterator();
	}

	@Override
	default Object[] toArray() {
		return getSource().toArray();
	}

	@Override
	default <T> T[] toArray(T[] a) {
		return getSource().toArray(a);
	}

	@Override
	default boolean add(E e) {
		return getSource().add(e);
	}

	@Override
	default boolean remove(Object o) {
		return getSource().remove(o);
	}

	@Override
	default boolean containsAll(Collection<?> c) {
		return getSource().containsAll(c);
	}

	@Override
	default boolean addAll(Collection<? extends E> c) {
		return getSource().addAll(c);
	}

	@Override
	default boolean retainAll(Collection<?> c) {
		return getSource().retainAll(c);
	}

	@Override
	default void clear() {
		getSource().clear();
	}


	@Override
	default Stream<E> parallelStream() {
		return getSource().parallelStream();
	}

	@Override
	default boolean removeIf(Predicate<? super E> filter) {
		return getSource().removeIf(filter);
	}

	@Override
	default Spliterator<E> spliterator() {
		return getSource().spliterator();
	}

	@Override
	default Stream<E> stream() {
		return getSource().stream();
	}

	@Override
	default boolean removeAll(Collection<?> c) {
		return getSource().removeAll(c);
	}
}
