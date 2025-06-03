package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SequentialIterator<E> implements Iterator<Sequential<E>> {
	@NonNull
	private final Iterator<? extends E> iterator;
	private long index = 0;

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Sequential<E> next() {
		if (!iterator.hasNext()) {
			throw new NoSuchElementException();
		}

		E value = iterator.next();
		return new Sequential<>(index++, value, !iterator.hasNext());
	}
}
