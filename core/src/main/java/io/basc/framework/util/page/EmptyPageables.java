package io.basc.framework.util.page;

import java.util.NoSuchElementException;

public class EmptyPageables<K, T> extends SharedPageable<K, T> implements Pageables<K, T> {
	private static final long serialVersionUID = 1L;

	@Override
	public Pageables<K, T> next() {
		throw new NoSuchElementException("next");
	}

	@Override
	public Pageables<K, T> jumpTo(K cursorId) {
		throw new NoSuchElementException("jumpTo");
	}
}
