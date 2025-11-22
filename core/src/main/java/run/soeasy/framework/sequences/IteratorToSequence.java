package run.soeasy.framework.sequences;

import java.util.Iterator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class IteratorToSequence<T> implements Sequence<T> {
	private final Iterator<? extends T> iterator;

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public @NonNull T next() {
		return iterator.next();
	}
}
