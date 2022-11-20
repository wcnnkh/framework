package io.basc.framework.util;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Cursors<E> extends AbstractCursor<E, Cursor<E>> {
	private BigInteger position = BigInteger.ZERO;
	private final Iterator<? extends Cursor<E>> sources;
	private Cursor<E> currentCursor;

	public Cursors(Iterable<? extends Cursor<E>> sources) {
		Assert.requiredArgument(sources != null, "sources");
		this.sources = sources.iterator();
	}

	public Cursors(Iterator<? extends Cursor<E>> sources) {
		Assert.requiredArgument(sources != null, "sources");
		this.sources = sources;
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
				super.onClose((e) -> currentCursor.close(e));
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
	public void remove() {
		if (currentCursor != null) {
			currentCursor.remove();
		}
	}
}
