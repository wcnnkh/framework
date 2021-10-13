package io.basc.framework.util.page;

import java.util.List;
import java.util.stream.Stream;

public class StreamPages<T> implements Pages<T> {
	private final Page<T> page;
	private final CursorProcessor<Long, T> cursorProcessor;

	public StreamPages(Page<T> page, CursorProcessor<Long, T> cursorProcessor) {
		this.page = page;
		this.cursorProcessor = cursorProcessor;
	}

	public StreamPages(long total, long cursorId, long count, CursorProcessor<Long, T> cursorProcessor) {
		this(new StreamPage<>(cursorId, () -> cursorProcessor.process(cursorId, count), count, total), cursorProcessor);
	}

	@Override
	public long getTotal() {
		return page.getTotal();
	}

	@Override
	public Long getCursorId() {
		return page.getCursorId();
	}

	@Override
	public long getCount() {
		return page.getCount();
	}

	@Override
	public Long getNextCursorId() {
		return page.getNextCursorId();
	}

	@Override
	public List<T> rows() {
		return page.rows();
	}

	@Override
	public boolean hasNext() {
		return page.hasNext();
	}

	@Override
	public Pages<T> jumpTo(Long cursorId) {
		return new StreamPages<>(getTotal(), cursorId, getCount(), cursorProcessor);
	}

	@Override
	public Stream<T> stream() {
		return page.stream();
	}

}
