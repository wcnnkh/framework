package io.basc.framework.util.page;

import java.util.function.Supplier;

import io.basc.framework.util.Cursor;

public class StreamPage<K, T> extends StreamPageable<K, T> implements Page<K, T> {
	private final long total;
	private final long count;

	public StreamPage(K cursorId, Supplier<? extends Cursor<T>> cursorSupplier, K nextCursorId, long count,
			long total) {
		super(cursorId, cursorSupplier, nextCursorId);
		this.total = total;
		this.count = count;
	}

	@Override
	public long getTotal() {
		return total;
	}

	@Override
	public long getCount() {
		return count;
	}

}
