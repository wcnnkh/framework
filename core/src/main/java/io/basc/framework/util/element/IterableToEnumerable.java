package io.basc.framework.util.element;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

import io.basc.framework.util.Assert;
import lombok.Data;

@Data
public class IterableToEnumerable<S, E> implements Enumerable<E> {
	private final Iterable<? extends S> iterable;
	private final Function<? super S, ? extends E> converter;

	public IterableToEnumerable(Iterable<? extends S> iterable, Function<? super S, ? extends E> converter) {
		Assert.requiredArgument(iterable != null, "iterable");
		Assert.requiredArgument(converter != null, "converter");
		this.iterable = iterable;
		this.converter = converter;
	}

	@Override
	public Enumeration<E> enumeration() {
		Iterator<? extends S> iterator = iterable.iterator();
		if (iterator == null) {
			return null;
		}

		return new IteratorToEnumeration<>(iterator, converter);
	}

}
