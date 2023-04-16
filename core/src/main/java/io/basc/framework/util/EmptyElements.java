package io.basc.framework.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;

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
}
