package io.basc.framework.util;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ElementsWrapper<E, W extends Elements<E>> extends Elements<E>, StreamableWrapper<E, W> {

	@Override
	default ServiceLoader<E> cacheable() {
		return getSource().cacheable();
	}

	@Override
	default Elements<E> concat(Elements<? extends E> elements) {
		return getSource().concat(elements);
	}

	@Override
	default <U> Elements<U> convert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
		return getSource().convert(converter);
	}

	@Override
	default Elements<E> distinct() {
		return getSource().distinct();
	}

	@Override
	default Enumeration<E> enumeration() {
		return getSource().enumeration();
	}

	@Override
	default Elements<E> exclude(Predicate<? super E> predicate) {
		return getSource().exclude(predicate);
	}

	@Override
	default Elements<E> filter(Predicate<? super E> predicate) {
		return getSource().filter(predicate);
	}

	@Override
	default <U> Elements<U> flatMap(Function<? super E, ? extends Streamable<U>> mapper) {
		return getSource().flatMap(mapper);
	}

	@Override
	default Elements<Indexed<E>> index() {
		return getSource().index();
	}

	@Override
	default Elements<IterativeElement<E>> iterative() {
		return getSource().iterative();
	}

	@Override
	default Iterator<E> iterator() {
		return getSource().iterator();
	}

	@Override
	default Elements<E> limit(long maxSize) {
		return getSource().limit(maxSize);
	}

	@Override
	default <U> Elements<U> map(Function<? super E, ? extends U> mapper) {
		return getSource().map(mapper);
	}

	@Override
	default Elements<E> reverse() {
		return getSource().reverse();
	}

	@Override
	default Elements<E> skip(long n) {
		return getSource().skip(n);
	}

	@Override
	default Elements<E> sorted() {
		return getSource().sorted();
	}

	@Override
	default Elements<E> sorted(Comparator<? super E> comparator) {
		return getSource().sorted(comparator);
	}

	@Override
	default Spliterator<E> spliterator() {
		return getSource().spliterator();
	}

	@Override
	default ListElementsWrapper<E, ? extends List<E>> toList() {
		return getSource().toList();
	}

	@Override
	default SetElementsWrapper<E, ? extends Set<E>> toSet() {
		return getSource().toSet();
	}

	@Override
	default Elements<E> unordered() {
		return getSource().unordered();
	}

	@Override
	default E get(long index) throws IndexOutOfBoundsException {
		return getSource().get(index);
	}

	@Override
	default void forEach(Consumer<? super E> action) {
		getSource().forEach(action);
	}
}
