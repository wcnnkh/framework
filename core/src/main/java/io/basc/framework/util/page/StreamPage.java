package io.basc.framework.util.page;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamPage<K, T> extends StreamPageable<K, T> implements Page<K, T> {
	private final long total;
	private final long count;

	public StreamPage(K cursorId, Supplier<Stream<T>> stream, K nextCursorId, long count, long total) {
		super(cursorId, stream, nextCursorId);
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
