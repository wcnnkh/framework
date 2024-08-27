package io.basc.framework.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class IterationIterator<S, T> implements Iterator<T> {
	private final Iterator<? extends S> iterator;
	private final Function<? super S, ? extends Iterator<? extends T>> converter;
	private Iterator<? extends T> valueIterator;

	public IterationIterator(Iterator<? extends S> iterator,
			Function<? super S, ? extends Iterator<? extends T>> converter) {
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.requiredArgument(converter != null, "converter");
		this.iterator = iterator;
		this.converter = converter;
	}

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
