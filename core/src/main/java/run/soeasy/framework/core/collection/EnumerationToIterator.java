package run.soeasy.framework.core.collection;

import java.util.Enumeration;
import java.util.Iterator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EnumerationToIterator<E> implements Iterator<E> {
	private final Enumeration<? extends E> enumeration;

	@Override
	public boolean hasNext() {
		return enumeration.hasMoreElements();
	}

	@Override
	public E next() {
		return enumeration.nextElement();
	}

}
