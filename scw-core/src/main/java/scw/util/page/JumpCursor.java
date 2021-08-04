package scw.util.page;

import java.util.Iterator;

public class JumpCursor<K, T> implements Cursor<K, T> {
	private final Pageable<K, T> pageable;

	public JumpCursor(Pageable<K, T> pageable) {
		this.pageable = pageable;
	}

	@Override
	public boolean isClosed() {
		if (pageable instanceof Cursor) {
			return ((Cursor<?, ?>) pageable).isClosed();
		}
		return false;
	}

	@Override
	public void close() {
		if (pageable instanceof Cursor) {
			((Cursor<?, ?>) pageable).close();
		}
	}

	@Override
	public K getCursorId() {
		return pageable.getCursorId();
	}

	@Override
	public Long getCount() {
		return pageable.getCount();
	}

	@Override
	public K getNextCursorId() {
		return pageable.getCursorId();
	}

	@Override
	public boolean hasNext() {
		return pageable.hasNext();
	}

	@Override
	public Iterator<T> iterator() {
		return pageable.iterator();
	}

	@Override
	public long getCreateTime() {
		return pageable.getCreateTime();
	}
}
