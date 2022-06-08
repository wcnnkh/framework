package io.basc.framework.convert;

import java.util.Iterator;
import java.util.function.Function;

import io.basc.framework.util.AbstractIterator;
import io.basc.framework.util.Assert;

public class ConvertibleIterator<T, V> extends AbstractIterator<V> {
	private Iterator<? extends T> iterator;
	private Function<T, V> converter;

	public ConvertibleIterator(Iterator<? extends T> iterator, Function<T, V> converter) {
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.requiredArgument(converter != null, "converter");
		this.iterator = iterator;
		this.converter = converter;
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public V next() {
		return converter.apply(iterator.next());
	}
}
