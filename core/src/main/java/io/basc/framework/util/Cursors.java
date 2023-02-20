package io.basc.framework.util;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Cursors<E> extends AbstractCursor<E, Cursors<E>> {
	private BigInteger position = BigInteger.ZERO;
	private final ReversibleIterator<? extends Cursor<E>> sources;
	private Cursor<E> currentCursor;

	public Cursors(Iterator<? extends Cursor<E>> sources) {
		Assert.requiredArgument(sources != null, "sources");
		this.sources = Cursor.of(sources);
	}

	public Cursors(List<? extends Cursor<E>> sources) {
		Assert.requiredArgument(sources != null, "sources");
		this.sources = Cursor.of(sources);
	}

	@Override
	public BigInteger getPosition() {
		return position;
	}

	@Override
	public boolean hasNext() {
		if (currentCursor == null || !currentCursor.hasNext()) {
			while (sources.hasNext()) {
				currentCursor = sources.next();
				position = position.add(currentCursor.getPosition());
				super.onClose(() -> currentCursor.close());
				if (currentCursor.hasNext()) {
					break;
				}
			}
		}
		return currentCursor != null && currentCursor.hasNext();
	}

	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		try {
			return currentCursor.next();
		} finally {
			position = position.add(BigInteger.ONE);
		}
	}

	@Override
	public boolean hasPrevious() {
		if (position.compareTo(BigInteger.ZERO) <= 0) {
			return false;
		}

		if (currentCursor == null || !currentCursor.hasPrevious()) {
			while (sources.hasPrevious()) {
				currentCursor = sources.previous();
				position = position.subtract(currentCursor.getPosition());
				super.onClose(() -> currentCursor.close());
				if (currentCursor.hasPrevious()) {
					break;
				}
			}
		}
		return currentCursor != null && currentCursor.hasPrevious();
	}

	@Override
	public E previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException();
		}

		try {
			return currentCursor.previous();
		} finally {
			position = position.subtract(BigInteger.ONE);
		}
	}

	@Override
	public void remove() {
		if (currentCursor != null) {
			currentCursor.remove();
		}
	}
}
