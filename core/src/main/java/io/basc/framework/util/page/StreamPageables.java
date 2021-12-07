package io.basc.framework.util.page;

import java.util.List;

import io.basc.framework.util.stream.Processor;

public class StreamPageables<K, T> implements Pageables<K, T> {
	private final Pageable<K, T> pageable;
	private final Processor<K, Pageable<K, T>, RuntimeException> processor;

	public StreamPageables(Pageable<K, T> pageable, Processor<K, Pageable<K, T>, RuntimeException> processor) {
		this.pageable = pageable;
		this.processor = processor;
	}

	public StreamPageables(K cursorId, Processor<K, Pageable<K, T>, RuntimeException> processor) {
		this.pageable = processor.process(cursorId);
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
		Pageable<K, T> jumpTo = processor.process(cursorId);
		return new StreamPageables<>(jumpTo, processor);
	}
}
