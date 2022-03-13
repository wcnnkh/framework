package io.basc.framework.util;

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
	private final Iterator<E> iterator;
	private final long start;
	private final long limit;
	private long current = 0;

	/**
	 * @param iterator
	 * @param start
	 */
	public LimitIterator(Iterator<E> iterator, long start) {
		this(iterator, start, -1);
	}

	/**
	 * @param iterator
	 * @param start
	 * @param limit    小于0 则不做限制
	 */
	public LimitIterator(Iterator<E> iterator, long start, long limit) {
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.isTrue(start >= 0, "required start >= 0");
		this.iterator = iterator;
		this.start = start;
		this.limit = limit;
	}

	@Override
	public boolean hasNext() {
		if (limit > 0 && (current - start) > limit) {
			return false;
		}

		for (; current < start && iterator.hasNext(); current++, iterator.next()) {
			// ignore
		}
		return iterator.hasNext();
	}

	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return iterator.next();
	}

	@Override
	public void remove() {
		hasNext();
		iterator.remove();
	}
}
