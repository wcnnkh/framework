package scw.util.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class SharedPageable<K, T> implements Pageable<K, T>, Serializable {
	private static final long serialVersionUID = 1L;
	private final List<T> list;
	private final K cursorId;
	private final K nextCursorId;
	private final long count;
	private final boolean hasNext;

	public SharedPageable(K cursorId, List<T> list, K nextCursorId, long count, boolean hasNext) {
		this.cursorId = cursorId;
		this.nextCursorId = nextCursorId;
		this.list = list;
		this.count = count;
		this.hasNext = hasNext;
	}

	@Override
	public List<T> rows() {
		return Collections.unmodifiableList(list);
	}

	@Override
	public K getCursorId() {
		return cursorId;
	}

	@Override
	public long getCount() {
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
}
