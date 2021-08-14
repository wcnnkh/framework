package scw.util.page;

import java.util.stream.Stream;

public class StreamPage<T> extends StreamPageable<Long, T> implements Page<T> {
	private final long total;

	public StreamPage(long cursorId, Stream<T> stream, long count, long total) {
		this(cursorId, stream, PageSupport.getNextStart(cursorId, count),
				count, total, PageSupport.hasMore(total, count, cursorId));
	}

	public StreamPage(long cursorId, Stream<T> stream, long nextCursorId,
			long count, long total, boolean hasNext) {
		super(cursorId, stream, nextCursorId, count, hasNext);
		this.total = total;
	}

	@Override
	public long getTotal() {
		return total;
	}

}
