package io.basc.framework.util.page;

import java.util.List;

public class StreamPageables<K, T> implements Pageables<K, T> {
	private final Pageable<K, T> pageable;
	private final PageableProcessor<K, T> pageableProcessor;

	public StreamPageables(Pageable<K, T> pageable, PageableProcessor<K, T> pageableProcessor) {
		this.pageable = pageable;
		this.pageableProcessor = pageableProcessor;
	}

	@Override
	public K getCursorId() {
		return pageable.getCursorId();
	}

	@Override
	public long getCount() {
		return pageable.getCount();
	}

	@Override
	public K getNextCursorId() {
		return pageable.getNextCursorId();
	}

	@Override
	public List<T> rows() {
		return pageable.rows();
	}

	@Override
	public Pageables<K, T> jumpTo(K cursorId) {
		Pageable<K, T> jumpTo = pageableProcessor.process(cursorId, getCount());
		return new StreamPageables<>(jumpTo, pageableProcessor);
	}

}
