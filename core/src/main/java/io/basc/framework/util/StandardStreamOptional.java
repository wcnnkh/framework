package io.basc.framework.util;

import java.util.NoSuchElementException;
import java.util.function.Function;

import io.basc.framework.lang.Nullable;

public class StandardStreamOptional<T> extends AbstractStreamOptional<T, StreamOptional<T>>
		implements StreamOptional<T> {
	private final Source<? extends T, ? extends RuntimeException> source;

	public StandardStreamOptional(Source<? extends T, ? extends RuntimeException> source) {
		this(source, null);
	}

	public StandardStreamOptional(Source<? extends T, ? extends RuntimeException> source,
			@Nullable RunnableProcessor<? extends RuntimeException> closeHandler) {
		this(source, null, closeHandler);
	}

	public StandardStreamOptional(Source<? extends T, ? extends RuntimeException> source,
			@Nullable ConsumeProcessor<? super T, ? extends RuntimeException> closeProcessor,
			@Nullable RunnableProcessor<? extends RuntimeException> closeHandler) {
		super(closeProcessor, closeHandler);
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
