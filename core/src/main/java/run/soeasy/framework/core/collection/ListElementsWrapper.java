package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface ListElementsWrapper<E, W extends List<E>> extends ListWrapper<E, W>, CollectionElementsWrapper<E, W> {

	@Override
	default boolean contains(Object o) {
		return ListWrapper.super.contains(o);
	}

	@Override
	default void forEach(Consumer<? super E> action) {
		ListWrapper.super.forEach(action);
	}

	@Override
	default E get(int index) throws IndexOutOfBoundsException {
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
	default boolean isEmpty() {
		return ListWrapper.super.isEmpty();
	}

	@Override
	default Iterator<E> iterator() {
		return ListWrapper.super.iterator();
	}

	@Override
	default Elements<E> reverse() {
		return Elements.of(() -> CollectionUtils.getIterator(getSource(), true));
	}

	@Override
	default Stream<E> stream() {
		return ListWrapper.super.stream();
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
	default ListElementsWrapper<E, W> toList() {
		return this;
	}

}