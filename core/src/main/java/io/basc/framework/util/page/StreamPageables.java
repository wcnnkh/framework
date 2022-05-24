package io.basc.framework.util.page;

import java.util.List;
import java.util.function.Function;

public class StreamPageables<K, T> implements Pageables<K, T> {
	private final Pageable<K, T> pageable;
	private final Function<K, ? extends Pageable<K, T>> processor;

	public StreamPageables(Pageable<K, T> pageable, Function<K, ? extends Pageable<K, T>> processor) {
		this.pageable = pageable;
		this.processor = processor;
	}

	public StreamPageables(K cursorId, Function<K, ? extends Pageable<K, T>> processor) {
		this.pageable = processor.apply(cursorId);
		this.processor = processor;
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
	public List<T> getList() {
		return pageable.getList();
	}

	@Override
	public StreamPageables<K, T> jumpTo(K cursorId) {
		Pageable<K, T> jumpTo = processor.apply(cursorId);
		return new StreamPageables<>(jumpTo, processor);
	}
}
