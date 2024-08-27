package io.basc.framework.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class IterativeElementIterator<E> implements Iterator<IterativeElement<E>> {
	@NonNull
	private final Iterator<E> iterator;

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public IterativeElement<E> next() {
		if (!iterator.hasNext()) {
			throw new NoSuchElementException();
		}

		E value = iterator.next();
		return new IterativeElement<>(value, iterator.hasNext());
	}

}
