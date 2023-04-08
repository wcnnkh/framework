package io.basc.framework.util;

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
	public <U> Elements<U> convert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
		return wrappedTarget.convert(converter);
	}

	@Override
	public long count() {
		return wrappedTarget.count();
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
	public boolean isEmpty() {
		return wrappedTarget.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return wrappedTarget.iterator();
	}

	@Override
	public <U> Elements<U> map(Function<? super E, ? extends U> mapper) {
		return wrappedTarget.map(mapper);
	}

	@Override
	public Spliterator<E> spliterator() {
		return wrappedTarget.spliterator();
	}
}
