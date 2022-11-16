package io.basc.framework.util;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 对数量进行限制
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public final class LimitIterator<E> implements Iterator<E> {
	private final Iterator<? extends E> iterator;
	private BigInteger start;
	private final BigInteger end;

	/**
	 * @param iterator
	 * @param start
	 */
	public LimitIterator(Iterator<E> iterator, BigInteger start) {
		this(iterator, start, BigInteger.ZERO);
	}

	/**
	 * @param iterator
	 * @param start
	 * @param end      小于 0 则不做限制
	 */
	public LimitIterator(Iterator<? extends E> iterator, BigInteger start, BigInteger end) {
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.isTrue(end != null && start.compareTo(BigInteger.ZERO) >= 0, "required start >= 0");
		this.iterator = iterator;
		this.start = start;
		this.end = end;
	}

	public boolean hasNextPosition() {
		return end == null || end.compareTo(BigInteger.ZERO) < 0 || end.compareTo(start) > 0;
	}

	@Override
	public boolean hasNext() {
		if (!hasNextPosition()) {
			return false;
		}

		for (BigInteger i = BigInteger.ZERO; start.compareTo(i) > 0; i = i.add(BigInteger.ONE)) {
			if (!iterator.hasNext()) {
				break;
			}

			iterator.next();
		}
		return iterator.hasNext();
	}

	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		try {
			return iterator.next();
		} finally {
			this.start = this.start.add(BigInteger.ONE);
		}
	}

	@Override
	public void remove() {
		hasNext();
		iterator.remove();
	}
}
