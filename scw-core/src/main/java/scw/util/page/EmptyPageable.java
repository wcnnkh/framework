package scw.util.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class EmptyPageable<K, T> implements Pageable<K, T>, Serializable {
	private static final long serialVersionUID = 1L;
	private final K cursorId;
	private final long count;

	public EmptyPageable(K cursorId, long count) {
		this.cursorId = cursorId;
		this.count = count;
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
		return null;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public List<T> rows() {
		return Collections.emptyList();
	}

}
