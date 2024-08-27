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
	public Stream<E> stream() {
		return Stream.empty();
	}

	@Override
	public ServiceLoader<E> cacheable() {
		return ServiceLoader.empty();
	}

	@Override
	public ListElementsWrapper<E, ?> toList() {
		return new StandardListElements<>(Collections.emptyList());
	}

	@Override
	public SetElementsWrapper<E, ?> toSet() {
		return new StandardSetElements<>(Collections.emptySet());
	}
}
