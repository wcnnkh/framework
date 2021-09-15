package io.basc.framework.util.page;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamPage<T> extends StreamPageable<Long, T> implements Page<T> {
	private final long total;

	public StreamPage(long cursorId, Supplier<Stream<T>> stream, long count, long total) {
		this(cursorId, stream, PageSupport.getNextStart(cursorId, count), count, total);
	}

	public StreamPage(long cursorId, Supplier<Stream<T>> stream, long nextCursorId, long count, long total) {
		super(cursorId, stream, nextCursorId, count);
		this.total = total;
	}

	@Override
	public long getTotal() {
		return total;
	}

}
