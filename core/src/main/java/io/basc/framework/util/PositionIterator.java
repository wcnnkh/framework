package io.basc.framework.util;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.function.Function;

public class PositionIterator<E> implements Iterator<E> {
	private BigInteger start;
	private final BigInteger end;
	private final Function<? super BigInteger, ? extends E> getter;

	public PositionIterator(BigInteger end, Function<? super BigInteger, ? extends E> getter) {
		this(BigInteger.ZERO, end, getter);
	}

	public BigInteger getPosition() {
		return start;
	}

	/**
	 * 前包后闭
	 * 
	 * @param start
	 * @param end
	 * @param getter
	 */
	public PositionIterator(BigInteger start, BigInteger end, Function<? super BigInteger, ? extends E> getter) {
		Assert.requiredArgument(start != null, "start");
		Assert.requiredArgument(end != null, "end");
		Assert.requiredArgument(getter != null, "getter");
		this.start = start;
		this.end = end;
		this.getter = getter;
	}

	@Override
	public boolean hasNext() {
		return end.compareTo(start) > 0;
	}

	@Override
	public E next() {
		try {
			return getter.apply(start);
		} finally {
			this.start = this.start.add(BigInteger.ONE);
		}
	}

}
