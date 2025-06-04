package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public interface ListWrapper<E, W extends List<E>> extends List<E>, CollectionWrapper<E, W> {

	@Override
	default boolean add(E e) {
		return getSource().add(e);
	}

	@Override
	default void add(int index, E element) {
		getSource().add(index, element);
	}

	@Override
	default boolean addAll(Collection<? extends E> c) {
		return getSource().addAll(c);
	}

	@Override
	default boolean addAll(int index, Collection<? extends E> c) {
		return getSource().addAll(index, c);
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
	default E get(int index) {
		return getSource().get(index);
	}

	@Override
	default int indexOf(Object o) {
		return getSource().indexOf(o);
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
	default int lastIndexOf(Object o) {
		return getSource().lastIndexOf(o);
	}

	@Override
	default ListIterator<E> listIterator() {
		return getSource().listIterator();
	}

	@Override
	default ListIterator<E> listIterator(int index) {
		return getSource().listIterator(index);
	}

	@Override
	default Stream<E> parallelStream() {
		return getSource().parallelStream();
	}

	@Override
	default E remove(int index) {
		return getSource().remove(index);
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
	default void replaceAll(UnaryOperator<E> operator) {
		getSource().replaceAll(operator);
	}

	@Override
	default boolean retainAll(Collection<?> c) {
		return getSource().retainAll(c);
	}

	@Override
	default E set(int index, E element) {
		return getSource().set(index, element);
	}

	@Override
	default int size() {
		return getSource().size();
	}

	@Override
	default void sort(Comparator<? super E> c) {
		getSource().sort(c);
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
	default List<E> subList(int fromIndex, int toIndex) {
		return getSource().subList(fromIndex, toIndex);
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