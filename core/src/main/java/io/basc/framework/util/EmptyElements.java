package io.basc.framework.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EmptyElements<E> extends EmptyStreamable<E> implements Elements<E> {
	private static final long serialVersionUID = 1L;
	public static final EmptyElements<Object> INSTANCE = new EmptyElements<>();

	@Override
	public Iterator<E> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Elements<E> clone() {
		return this;
	}

	@Override
	public Elements<E> filter(Predicate<? super E> predicate) {
		return this;
	}

	@Override
	public Elements<E> reverse() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> Elements<U> map(Function<? super E, ? extends U> mapper) {
		return (Elements<U>) this;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public ElementList<E> toList() {
		return new ElementList<>(Collections.emptyList());
	}

	@Override
	public ElementSet<E> toSet() {
		return new ElementSet<>(Collections.emptySet());
	}

	@Override
	public Elements<E> cacheable() {
		return this;
	}

	@Override
	public Stream<E> stream() {
		return Stream.empty();
	}

}
