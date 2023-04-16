package io.basc.framework.util;

import java.io.Serializable;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EmptyStreamable<E> implements Streamable<E>, Serializable {
	private static final long serialVersionUID = 1L;
	public static final EmptyStreamable<Object> INSTANCE = new EmptyStreamable<>();

	@Override
	public Stream<E> stream() {
		return Stream.empty();
	}

	@Override
	public Streamable<E> filter(Predicate<? super E> predicate) {
		return this;
	}
}
