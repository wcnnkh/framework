package io.basc.framework.util.page;

import java.util.List;
import java.util.stream.Stream;

public class DefaultPages<T> implements Pages<T> {
	private final Page<T> page;
	private final CursorProcessor<Long, T> cursorProcessor;

	public DefaultPages(Page<T> page, CursorProcessor<Long, T> cursorProcessor) {
		this.page = page;
		this.cursorProcessor = cursorProcessor;
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
		Page<T> page = new StreamPage<>(cursorId, () -> cursorProcessor.process(cursorId, this.page.getCount()), this.page.getCount(),
				this.page.getTotal());
		return new DefaultPages<>(page, cursorProcessor);
	}

	@Override
	public Stream<T> stream() {
		return page.stream();
	}

}
