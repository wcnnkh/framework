package io.basc.framework.util.page;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamPagination<T> extends StreamPage<Long, T> implements Pagination<T> {

	public StreamPagination(long cursorId, Supplier<Stream<T>> stream, long count, long total) {
		super(cursorId, stream, PageSupport.getNextStart(cursorId, count), count, total);
	}

	public StreamPagination(Long cursorId, Supplier<Stream<T>> stream, Long nextCursorId, long count, long total) {
		super(cursorId, stream, nextCursorId, count, total);
	}
}
