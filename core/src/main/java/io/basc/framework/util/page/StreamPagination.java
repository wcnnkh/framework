package io.basc.framework.util.page;

import java.util.function.Supplier;

import io.basc.framework.util.Cursor;

public class StreamPagination<T> extends StreamPage<Long, T> implements Pagination<T> {

	public StreamPagination(long cursorId, Supplier<? extends Cursor<T>> cursorSupplier, long count, long total) {
		super(cursorId, cursorSupplier, PageSupport.getNextStart(cursorId, count), count, total);
	}

	public StreamPagination(Long cursorId, Supplier<? extends Cursor<T>> cursorSupplier, Long nextCursorId, long count,
			long total) {
		super(cursorId, cursorSupplier, nextCursorId, count, total);
	}
}
