package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface SetWrapper<E, W extends Set<E>> extends Set<E>, CollectionWrapper<E, W> {

	@Override
	default boolean add(E e) {
		return getSource().add(e);
	}

	@Override
	default boolean addAll(Collection<? extends E> c) {
		return getSource().addAll(c);
	}

	@Override
	default void clear() {
		getSource().clear();
	}

	@Override
	default boolean contains(Object o) {
		return getSource().contains(o);
	}

	@Override
	default boolean containsAll(Collection<?> c) {
		return getSource().containsAll(c);
	}

	@Override
	default void forEach(Consumer<? super E> action) {
		getSource().forEach(action);
	}

	@Override
	default boolean isEmpty() {
		return getSource().isEmpty();
	}

	@Override
	default Iterator<E> iterator() {
		return getSource().iterator();
	}

	@Override
	default Stream<E> parallelStream() {
		return getSource().parallelStream();
	}

	@Override
	default boolean remove(Object o) {
		return getSource().remove(o);
	}

	@Override
	default boolean removeAll(Collection<?> c) {
		return getSource().removeAll(c);
	}

	@Override
	default boolean removeIf(Predicate<? super E> filter) {
		return getSource().removeIf(filter);
	}

	@Override
	default boolean retainAll(Collection<?> c) {
		return getSource().retainAll(c);
	}

	@Override
	default int size() {
		return getSource().size();
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
	default Object[] toArray() {
		return getSource().toArray();
	}

	@Override
	default <T> T[] toArray(T[] a) {
		return getSource().toArray(a);
	}
}