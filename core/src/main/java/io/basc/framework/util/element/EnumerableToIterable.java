package io.basc.framework.util.element;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

import io.basc.framework.util.Assert;
import lombok.Data;

@Data
public class EnumerableToIterable<S, T> implements Iterable<T> {
	private final Enumerable<? extends S> enumerable;
	private final Function<? super S, ? extends T> converter;

	public EnumerableToIterable(Enumerable<? extends S> enumerable, Function<? super S, ? extends T> converter) {
		Assert.requiredArgument(enumerable != null, "enumerable");
		Assert.requiredArgument(converter != null, "converter");
		this.enumerable = enumerable;
		this.converter = converter;
	}

	@Override
	public Iterator<T> iterator() {
		Enumeration<? extends S> enumeration = enumerable.enumeration();
		if (enumeration == null) {
			return null;
		}
		return new EnumerationToIterator<>(enumeration, converter);
	}

}