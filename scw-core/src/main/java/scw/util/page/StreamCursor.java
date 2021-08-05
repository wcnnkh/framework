package scw.util.page;

import java.util.Iterator;
import java.util.stream.Stream;

public class StreamCursor<K, T> implements Cursor<K, T> {
	private final Stream<T> stream;
	private final long count;
	private final K cursorId;
	private final K nextCursorId;
	private final boolean hasNext;

	public StreamCursor(Stream<T> stream, long count, K cursorId,
			K nextCursorId, boolean hasNext) {
		this.stream = stream;
		this.count = count;
		this.cursorId = cursorId;
		this.hasNext = hasNext;
		this.nextCursorId = nextCursorId;
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

	@Override
	public Iterator<T> iterator() {
		return stream.iterator();
	}

	@Override
	public Stream<T> stream() {
		return stream;
	}

	@Override
	public void close() {
		stream.close();
	}

}
