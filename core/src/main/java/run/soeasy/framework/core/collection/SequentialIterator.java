package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.math.Counter;

@RequiredArgsConstructor
class SequentialIterator<E> implements Iterator<Sequential<E>> {
	@NonNull
	private final Iterator<? extends E> iterator;
	private final Counter index = new Counter();

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
		return new Sequential<>(index.getAndIncrement(), value, !iterator.hasNext());
	}
}
