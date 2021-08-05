package scw.util.page;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class SharedPageable<K, T> implements Pageable<K, T>, Serializable {
	private static final long serialVersionUID = 1L;
	private final List<T> list;
	private final K cursorId;
	private final K nextCursorId;
	private final long count;
	private final boolean hasNext;

	public SharedPageable(K cursorId, List<T> list, K nextCursorId,
			long count, boolean hasNext) {
		this.cursorId = cursorId;
		this.nextCursorId = nextCursorId;
		this.list = list;
		this.count = count;
		this.hasNext = hasNext;
	}
	
	@Override
	public Pageable<K, T> shared() {
		return this;
	}

	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}
	
	@Override
	public Stream<T> stream() {
		return list.stream();
	}
	
	@Override
	public List<T> rows() {
		return list;
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
