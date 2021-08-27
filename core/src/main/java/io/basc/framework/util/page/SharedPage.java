package io.basc.framework.util.page;

import java.util.List;

public class SharedPage<T> extends SharedPageable<Long, T> implements Page<T> {
	private static final long serialVersionUID = 1L;
	private long total;

	public SharedPage(long cursorId, List<T> list, long count, long total) {
		this(cursorId, list, PageSupport.getNextStart(cursorId, count), count, total,
				PageSupport.hasMore(total, count, cursorId));
	}

	public SharedPage(long cursorId, List<T> list, long nextCursorId, long count, long total, boolean hasMore) {
		super(cursorId, list, nextCursorId, count, hasMore);
		this.total = total;
	}

	@Override
	public Long getCursorId() {
		return Math.min(super.getCursorId(), total);
	}

	@Override
	public long getPages() {
		return PageSupport.getPages(getTotal(), getCount());
	}

	@Override
	public long getTotal() {
		return total;
	}

	@Override
	public long getPageNumber() {
		return PageSupport.getPageNumber(getCount(), getCursorId());
	}

}
