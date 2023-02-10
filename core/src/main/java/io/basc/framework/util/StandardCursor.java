package io.basc.framework.util;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class StandardCursor<E, C extends StandardCursor<E, C>> extends AbstractCursor<E, C> {
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
	public boolean hasPrevious() {
		if (isClosed()) {
			return false;
		}

		if (position.compareTo(BigInteger.ZERO) > 0 && iterator instanceof ReversibleIterator) {
			if (((ReversibleIterator<?>) iterator).hasPrevious()) {
				return true;
			}
		}

		close();
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException(getClass().getName() + "#previous");
		}

		try {
			return ((ReversibleIterator<E>) iterator).previous();
		} finally {
			position = position.subtract(BigInteger.ONE);
		}
	}

	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException(getClass().getName() + "#next");
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
