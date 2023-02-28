package io.basc.framework.util.page;

import java.math.BigInteger;
import java.util.NoSuchElementException;

import io.basc.framework.util.AbstractCursor;
import io.basc.framework.util.Cursor;

public class AllPageable<S extends Pageables<K, T>, K, T> implements Pageable<K, T> {
	protected final S source;

	public AllPageable(S source) {
		this.source = source;
	}

	@Override
	public K getCursorId() {
		return source.getCursorId();
	}

	@Override
	public K getNextCursorId() {
		return null;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public Cursor<T> iterator() {
		return new All(source);
	}

	private class All extends AbstractCursor<T, All> {
		private Pageables<K, T> pageables;
		private Cursor<T> cursor;
		private BigInteger position;

		public All(Pageables<K, T> pageables) {
			this.pageables = pageables;
			this.cursor = pageables.iterator();
			super.onClose(() -> cursor.close());
			position = cursor.getPosition();
		}

		@Override
		public BigInteger getPosition() {
			return position;
		}

		@Override
		public boolean hasNext() {
			if (cursor.hasNext()) {
				return true;
			}

			if (pageables.hasNext()) {
				pageables = pageables.next();
				cursor = pageables.iterator();
				super.onClose(() -> cursor.close());
				position = position.add(cursor.getPosition());
				return hasNext();
			}
			return false;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			try {
				return cursor.next();
			} finally {
				position = position.add(BigInteger.ONE);
			}

		}
	}

}
