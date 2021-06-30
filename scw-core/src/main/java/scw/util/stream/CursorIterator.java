package scw.util.stream;

import java.util.Iterator;

public class CursorIterator<E> implements Iterator<E> {
	private final Iterator<E> iterator;
	private final CursorPosition cursorPosition;

	public CursorIterator(Iterator<E> iterator, CursorPosition cursorPosition) {
		this.iterator = iterator;
		this.cursorPosition = cursorPosition;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public E next() {
		E e = iterator.next();
		cursorPosition.increment();
		return e;
	}
}
