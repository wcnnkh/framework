package io.basc.framework.util.page;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamPageable<K, T> implements Pageable<K, T> {
	private final Supplier<Stream<T>> stream;
	private final K cursorId;
	private final K nextCursorId;
	private final long count;

	public StreamPageable(K cursorId, Supplier<Stream<T>> stream, K nextCursorId,
			long count) {
		this.cursorId = cursorId;
		this.nextCursorId = nextCursorId;
		this.stream = stream;
		this.count = count;
	}

	@Override
	public List<T> rows() {
		return stream.get().collect(Collectors.toList());
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
	public Stream<T> stream() {
		return stream.get();
	}
}
