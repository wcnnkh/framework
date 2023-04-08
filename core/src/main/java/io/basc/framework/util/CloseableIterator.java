package io.basc.framework.util;

import java.util.NoSuchElementException;

public interface CloseableIterator<E> extends AutoCloseable, ReversibleIterator<E> {

	@Override
	void close();

	@Override
	default boolean hasPrevious() {
		return false;
	}

	@Override
	default E previous() {
		throw new NoSuchElementException(CloseableIterator.class.getName() + "#previous");
	}
}
