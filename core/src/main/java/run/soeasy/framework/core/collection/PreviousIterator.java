package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.ListIterator;

class PreviousIterator<E> implements Iterator<E> {
	private final ListIterator<E> listIterator;

	public PreviousIterator(ListIterator<E> listIterator) {
		this.listIterator = listIterator;
	}

	public boolean hasNext() {
		return listIterator.hasPrevious();
	}

	public E next() {
		return listIterator.previous();
	}

	@Override
	public void remove() {
		listIterator.remove();
	}
}