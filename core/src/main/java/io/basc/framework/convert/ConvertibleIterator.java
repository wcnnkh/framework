package io.basc.framework.convert;

import io.basc.framework.util.AbstractIterator;
import io.basc.framework.util.Assert;

import java.util.Iterator;

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
