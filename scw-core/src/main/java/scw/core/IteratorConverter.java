package scw.core;

import java.util.Iterator;

import scw.convert.Converter;
import scw.util.AbstractIterator;

public class IteratorConverter<T, V> extends AbstractIterator<V> {
	private Iterator<T> iterator;
	private Converter<T, V> converter;

	public IteratorConverter(Iterator<T> iterator, Converter<T, V> converter) {
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.requiredArgument(converter != null, "converter");
		this.iterator = iterator;
		this.converter = converter;
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public V next() {
		return converter.convert(iterator.next());
	}
}
