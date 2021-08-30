package io.basc.framework.util.page;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyPageables<K, T> extends EmptyPageable<K, T> implements
		Pageables<K, T> {
	private static final long serialVersionUID = 1L;

	public EmptyPageables(K cursorId, long count) {
		super(cursorId, count);
	}

	@Override
	public Pageables<K, T> next() {
		throw new NoSuchElementException();
	}

	@Override
	public Iterator<T> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Pageables<K, T> jumpTo(K cursorId) {
		throw new NoSuchElementException();
	}
}
