package scw.util.page;

import java.io.Serializable;
import java.util.Iterator;

public class SimpleCursor<K, T> implements Cursor<K, T>, Serializable {
	private static final long serialVersionUID = 1L;
	private final Iterable<T> iterable;
	private final K cursorId;
	private final K nextCursorId;
	private final Long count;
	private final boolean hasNext;
	private final long createTime;

	public SimpleCursor(K cursorId, Iterable<T> iterable, K nextCursorId, Long count, boolean hasNext) {
		this.cursorId = cursorId;
		this.nextCursorId = nextCursorId;
		this.iterable = iterable;
		this.count = count;
		this.hasNext = hasNext;
		this.createTime = System.currentTimeMillis();
	}

	@Override
	public Iterator<T> iterator() {
		return iterable.iterator();
	}

	@Override
	public K getCursorId() {
		return cursorId;
	}

	@Override
	public Long getCount() {
		return count;
	}

	@Override
	public K getNextCursorId() {
		return nextCursorId;
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public void close() {
	}

	@Override
	public long getCreateTime() {
		return createTime;
	}
}
