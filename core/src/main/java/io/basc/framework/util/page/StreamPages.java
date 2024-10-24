package io.basc.framework.util.page;

import java.util.stream.Stream;

import io.basc.framework.util.Cursor;

public class StreamPages<K, T> implements Pages<K, T> {
	private final Page<K, T> page;
	protected final PageableProcessor<K, T> processor;

	public StreamPages(Page<K, T> page, PageableProcessor<K, T> processor) {
		this.page = page;
		this.processor = processor;
	}

	public StreamPages(long total, K cursorId, long count, PageableProcessor<K, T> processor) {
		Pageable<K, T> pageable = processor.process(cursorId, count);
		this.page = new StreamPage<K, T>(cursorId, () -> pageable.iterator(), pageable.getNextCursorId(), count, total);
		this.processor = processor;
	}

	public PageableProcessor<K, T> getProcessor() {
		return processor;
	}

	@Override
	public long getTotal() {
		return page.getTotal();
	}

	@Override
	public K getCursorId() {
		return page.getCursorId();
	}

	@Override
	public long getCount() {
		return page.getCount();
	}

	@Override
	public K getNextCursorId() {
		return page.getNextCursorId();
	}

	@Override
	public Cursor<T> iterator() {
		return page.iterator();
	}

	@Override
	public boolean hasNext() {
		return page.hasNext();
	}

	@Override
	public Stream<T> stream() {
		return page.stream();
	}

	@Override
	public Pages<K, T> jumpTo(K cursorId, long count) {
		return new StreamPages<>(getTotal(), cursorId, count, processor);
	}

}
