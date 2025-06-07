package run.soeasy.framework.core.collection;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnumerableToIterable<S, T> implements Iterable<T> {
	@NonNull
	private final Enumerable<? extends S> enumerable;
	@NonNull
	private final Function<? super S, ? extends T> converter;

	@Override
	public Iterator<T> iterator() {
		Enumeration<? extends S> enumeration = enumerable.enumeration();
		if (enumeration == null) {
			return null;
		}
		return new EnumerationToIterator<>(enumeration, converter);
	}

}
