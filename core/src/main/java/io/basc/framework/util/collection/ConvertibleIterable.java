package io.basc.framework.util.collection;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

public class ConvertibleIterable<T, V> implements Iterable<V> {
	private final Iterable<? extends T> iterable;
	private final Function<T, V> converter;

	public ConvertibleIterable(Iterable<? extends T> iterable, Function<T, V> converter) {
		this.iterable = iterable;
		this.converter = converter;
	}

	@Override
	public Iterator<V> iterator() {
		if (iterable == null) {
			return Collections.emptyIterator();
		}

		return new ConvertibleIterator<T, V>(iterable.iterator(), converter);
	}

}
