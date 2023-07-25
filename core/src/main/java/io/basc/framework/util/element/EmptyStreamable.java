package io.basc.framework.util.element;

import java.io.Serializable;
import java.util.stream.Stream;

public class EmptyStreamable<E> implements Streamable<E>, Serializable {
	private static final long serialVersionUID = 1L;
	public static final EmptyStreamable<Object> INSTANCE = new EmptyStreamable<>();

	@Override
	public Stream<E> stream() {
		return Stream.empty();
	}
}
