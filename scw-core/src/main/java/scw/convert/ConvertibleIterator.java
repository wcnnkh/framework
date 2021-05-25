package scw.convert;

import java.util.Iterator;

import scw.core.Assert;
import scw.util.AbstractIterator;

public class ConvertibleIterator<T, V> extends AbstractIterator<V> {
	private Iterator<? extends T> iterator;
	private Converter<T, V> converter;

	public ConvertibleIterator(Iterator<? extends T> iterator, Converter<T, V> converter) {
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
