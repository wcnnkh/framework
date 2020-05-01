package scw.core;

import java.util.Iterator;

import scw.lang.NestedRuntimeException;

public class IteratorConvert<T, V> implements Iterator<V> {
	private Iterator<T> iterator;
	private Converter<T, V> converter;

	public IteratorConvert(Iterator<T> iterator, Converter<T, V> converter) {
		this.iterator = iterator;
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public V next() {
		try {
			return converter.convert(iterator.next());
		} catch (Exception e) {
			throw new NestedRuntimeException(e);
		}
	}
}
