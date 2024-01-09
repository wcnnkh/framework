package io.basc.framework.util.element;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ElementsWrapper<E, W extends Elements<E>> extends StreamableWrapper<E, W> implements Elements<E> {

	public ElementsWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Elements<E> cacheable() {
		return wrappedTarget.cacheable();
	}

	@Override
	public Elements<E> concat(Elements<? extends E> elements) {
		return wrappedTarget.concat(elements);
	}

	@Override
	public <U> Elements<U> convert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
		return wrappedTarget.convert(converter);
	}

	@Override
	public Elements<E> distinct() {
		return wrappedTarget.distinct();
	}

	@Override
	public Enumeration<E> enumeration() {
		return wrappedTarget.enumeration();
	}

	@Override
	public Elements<E> exclude(Predicate<? super E> predicate) {
		return wrappedTarget.exclude(predicate);
	}

	@Override
	public Elements<E> filter(Predicate<? super E> predicate) {
		return wrappedTarget.filter(predicate);
	}

	@Override
	public <U> Elements<U> flatMap(Function<? super E, ? extends Streamable<U>> mapper) {
		return wrappedTarget.flatMap(mapper);
	}

	@Override
	public Elements<Indexed<E>> index() {
		return wrappedTarget.index();
	}

	@Override
	public Elements<IterativeElement<E>> iterative() {
		return wrappedTarget.iterative();
	}

	@Override
	public Iterator<E> iterator() {
		return wrappedTarget.iterator();
	}

	@Override
	public Elements<E> limit(long maxSize) {
		return wrappedTarget.limit(maxSize);
	}

	@Override
	public <U> Elements<U> map(Function<? super E, ? extends U> mapper) {
		return wrappedTarget.map(mapper);
	}

	@Override
	public Elements<E> reverse() {
		return wrappedTarget.reverse();
	}

	@Override
	public Elements<E> skip(long n) {
		return wrappedTarget.skip(n);
	}

	@Override
	public Elements<E> sorted() {
		return wrappedTarget.sorted();
	}

	@Override
	public Elements<E> sorted(Comparator<? super E> comparator) {
		return wrappedTarget.sorted(comparator);
	}

	@Override
	public Spliterator<E> spliterator() {
		return wrappedTarget.spliterator();
	}

	@Override
	public ElementList<E> toList() {
		return wrappedTarget.toList();
	}

	@Override
	public ElementSet<E> toSet() {
		return wrappedTarget.toSet();
	}

	@Override
	public Elements<E> unordered() {
		return wrappedTarget.unordered();
	}
	
	@Override
	public E get(long index) throws IndexOutOfBoundsException {
		return wrappedTarget.get(index);
	}
}
