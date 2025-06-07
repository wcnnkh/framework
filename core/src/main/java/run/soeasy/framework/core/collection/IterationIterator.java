package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class IterationIterator<S, T> implements Iterator<T> {
	@NonNull
	private final Iterator<? extends S> iterator;
	@NonNull
	private final Function<? super S, ? extends Iterator<? extends T>> converter;
	private Iterator<? extends T> valueIterator;

	@Override
	public boolean hasNext() {
		if (valueIterator == null || !valueIterator.hasNext()) {
			while (iterator.hasNext()) {
				S s = iterator.next();
				if (s == null) {
					continue;
				}
				valueIterator = converter.apply(s);
				if (valueIterator == null) {
					continue;
				}

				if (valueIterator.hasNext()) {
					return true;
				}
			}
		}
		return valueIterator != null && valueIterator.hasNext();
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return valueIterator.next();
	}
}
