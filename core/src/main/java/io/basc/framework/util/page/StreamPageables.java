package io.basc.framework.util.page;

import java.util.function.Function;

import io.basc.framework.util.Cursor;

public class StreamPageables<K, T> implements Pageables<K, T> {
	private final Pageable<K, T> pageable;
	private final Function<K, ? extends Pageable<K, T>> processor;

	public StreamPageables(Pageable<K, T> pageable, Function<K, ? extends Pageable<K, T>> processor) {
		this.pageable = pageable;
		this.processor = processor;
	}

	public StreamPageables(K cursorId, Function<K, ? extends Pageable<K, T>> processor) {
		this(processor.apply(cursorId), processor);
	}

	@Override
	public K getCursorId() {
		return pageable.getCursorId();
	}

	@Override
	public K getNextCursorId() {
		return pageable.getNextCursorId();
	}

	@Override
	public Cursor<T> iterator() {
		return pageable.iterator();
	}

	@Override
	public Pageables<K, T> jumpTo(K cursorId) {
		Pageable<K, T> jumpTo = processor.apply(cursorId);
		return new StreamPageables<>(jumpTo, processor);
	}
}
