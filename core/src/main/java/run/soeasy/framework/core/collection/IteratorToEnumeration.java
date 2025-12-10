package run.soeasy.framework.core.collection;

import java.util.Enumeration;
import java.util.Iterator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class IteratorToEnumeration<E> implements Enumeration<E> {
	private final Iterator<? extends E> iterator;

	@Override
	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	@Override
	public E nextElement() {
		return iterator.next();
	}
}