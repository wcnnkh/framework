package io.basc.framework.util;

import java.math.BigInteger;
import java.util.function.Function;

import io.basc.framework.lang.Nullable;

public final class ConvertibleCursor<S, T> extends AbstractCursor<T, ConvertibleCursor<S, T>> {
	private final ReversibleIterator<? extends S> iterator;
	private final Function<? super S, ? extends T> converter;
	private BigInteger position;

	public ConvertibleCursor(ReversibleIterator<? extends S> iterator, Function<? super S, ? extends T> converter,
			BigInteger position, @Nullable RunnableProcessor<? extends RuntimeException> closeProcessor) {
		super(closeProcessor);
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.requiredArgument(converter != null, "converter");
		Assert.requiredArgument(position != null, "position");
		this.iterator = iterator;
		this.converter = converter;
		this.position = position;
	}

	@Override
	public BigInteger getPosition() {
		return position;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public T next() {
		try {
			return converter.apply(iterator.next());
		} finally {
			this.position = position.add(BigInteger.ONE);
		}
	}

	@Override
	public void remove() {
		iterator.remove();
	}

	@Override
	public boolean hasPrevious() {
		return iterator.hasPrevious();
	}

	@Override
	public T previous() {
		try {
			return converter.apply(iterator.previous());
		} finally {
			this.position = this.position.subtract(BigInteger.ONE);
		}
	}
}
