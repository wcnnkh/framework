package io.basc.framework.util;

import java.util.NoSuchElementException;
import java.util.function.Function;

public class StandardStreamOptional<T> extends AbstractStreamOptional<T, StreamOptional<T>>
		implements StreamOptional<T> {
	private final Source<? extends T, ? extends RuntimeException> source;

	public StandardStreamOptional(Source<? extends T, ? extends RuntimeException> source) {
		this.source = source;
	}

	@Override
	public T get() {
		T value = source.get();
		if (value == null) {
			throw new NoSuchElementException("No value present");
		}
		return value;
	}

	@Override
	public boolean isPresent() {
		return source.get() != null;
	}

	@Override
	public <U> StreamOptional<U> convert(Function<? super T, ? extends U> converter) {
		return new StandardStreamOptional<>(() -> converter.apply(source.get()));
	}
}
