package io.basc.framework.util;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

public interface CloseableIterator<E> extends AutoCloseable, ReversibleIterator<E> {

	@Override
	void close();

	@Override
	default Stream<E> stream() {
		return ReversibleIterator.super.stream().onClose(() -> close());
	}

	@Override
	default boolean hasPrevious() {
		return false;
	}

	@Override
	default E previous() {
		throw new NoSuchElementException(CloseableIterator.class.getName() + "#previous");
	}
}
