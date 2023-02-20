package io.basc.framework.util.page;

import java.util.function.Supplier;

import io.basc.framework.util.Cursor;

public class StreamPageable<K, T> implements Pageable<K, T> {
	private final Supplier<? extends Cursor<T>> cursorSupplier;
	private final K cursorId;
	private final K nextCursorId;

	public StreamPageable(K cursorId, Supplier<? extends Cursor<T>> cursorSupplier, K nextCursorId) {
		this.cursorId = cursorId;
		this.nextCursorId = nextCursorId;
		this.cursorSupplier = cursorSupplier;
	}

	@Override
	public K getCursorId() {
		return cursorId;
	}

	@Override
	public K getNextCursorId() {
		return nextCursorId;
	}

	@Override
	public Cursor<T> iterator() {
		return cursorSupplier.get();
	}
}