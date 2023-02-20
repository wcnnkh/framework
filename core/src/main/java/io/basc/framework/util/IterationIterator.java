package io.basc.framework.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class IterationIterator<S, T> implements Iterator<T> {
	private final Iterator<? extends S> iterator;
	private final Function<? super S, ? extends Iterator<T>> converter;
	private Iterator<? extends T> valueIterator;

	public IterationIterator(Iterator<? extends S> iterator, Function<? super S, ? extends Iterator<T>> converter) {
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.requiredArgument(converter != null, "converter");
		this.iterator = iterator;
		this.converter = converter;
	}

	@Override
	public boolean hasNext() {
		if (valueIterator == null) {
			if (!iterator.hasNext()) {
				return false;
			}
			S s = iterator.next();
			valueIterator = converter.apply(s);
		}

		if (valueIterator.hasNext()) {
			return true;
		}

		if (iterator.hasNext()) {
			valueIterator = null;
			return hasNext();
		}
		return false;
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return valueIterator.next();
	}
}
