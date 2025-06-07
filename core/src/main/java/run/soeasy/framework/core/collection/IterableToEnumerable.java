package run.soeasy.framework.core.collection;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class IterableToEnumerable<S, E> implements Enumerable<E> {
	@NonNull
	private final Iterable<? extends S> iterable;
	@NonNull
	private final Function<? super S, ? extends E> converter;

	@Override
	public Enumeration<E> enumeration() {
		Iterator<? extends S> iterator = iterable.iterator();
		if (iterator == null) {
			return null;
		}

		return new IteratorToEnumeration<>(iterator, converter);
	}

}
