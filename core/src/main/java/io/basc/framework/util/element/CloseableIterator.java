package io.basc.framework.util.element;

import java.util.Iterator;

public interface CloseableIterator<E> extends AutoCloseable, Iterator<E> {

	@Override
	void close();
}
