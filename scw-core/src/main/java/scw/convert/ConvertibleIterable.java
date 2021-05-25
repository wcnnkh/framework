package scw.convert;

import java.util.Collections;
import java.util.Iterator;

public class ConvertibleIterable<T, V> implements Iterable<V> {
	private final Iterable<? extends T> iterable;
	private final Converter<T, V> converter;

	public ConvertibleIterable(Iterable<? extends T> iterable, Converter<T, V> converter) {
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
