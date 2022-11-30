package io.basc.framework.util;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class StandardCursor<E, C extends Cursor<E>> extends AbstractCursor<E, C> {
	private BigInteger position;
	private final Iterator<? extends E> iterator;

	public StandardCursor(CloseableIterator<? extends E> iterator) {
		this(iterator, BigInteger.ZERO);
	}

	public StandardCursor(Iterator<? extends E> iterator) {
		this(iterator, BigInteger.ZERO);
	}

	public StandardCursor(CloseableIterator<? extends E> iterator, BigInteger position) {
		this((Iterator<? extends E>) iterator, position);
		super.onClose(() -> iterator.close());
	}

	public StandardCursor(Iterator<? extends E> iterator, BigInteger position) {
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.requiredArgument(position != null, "position");
		this.iterator = iterator;
		this.position = position;
	}

	@Override
	public BigInteger getPosition() {
		return position;
	}

	@Override
	public boolean hasNext() {
		if (isClosed()) {
			return false;
		}

		if (iterator.hasNext()) {
			return true;
		}

		close();
		return false;
	}

	@Override
	public E next() {
		if (isClosed()) {
			throw new NoSuchElementException("Cursor closed!");
		}

		try {
			return iterator.next();
		} finally {
			position = position.add(BigInteger.ONE);
		}
	}

	@Override
	public void remove() {
		iterator.remove();
	}
}
