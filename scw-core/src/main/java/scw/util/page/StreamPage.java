package scw.util.page;

import java.util.stream.Stream;

public class StreamPage<T> extends StreamCursor<Long, T> implements Page<T> {
	private final long total;

	public StreamPage(Stream<T> stream, long total, long count, long cursorId) {
		super(stream, count, cursorId, PageSupport
				.getNextStart(cursorId, count), PageSupport.hasMore(total,
				count, cursorId));
		this.total = total;
	}

	@Override
	public long getTotal() {
		return total;
	}
}
