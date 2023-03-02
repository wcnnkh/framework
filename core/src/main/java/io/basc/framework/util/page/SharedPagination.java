package io.basc.framework.util.page;

import java.util.List;

import io.basc.framework.lang.Nullable;

public class SharedPagination<T> extends SharedPage<Long, T> implements Pagination<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * 默认的构造方法
	 * 
	 */
	public SharedPagination() {
	}

	public SharedPagination(long start) {
		super(start);
	}

	public SharedPagination(long start, long count) {
		super(start, count);
	}

	public SharedPagination(long start, List<T> rows, long count, long total) {
		this(start, rows, null, count, total);
	}

	public SharedPagination(long start, List<T> rows, @Nullable Long nextStart, long count, long total) {
		super(start, rows, nextStart, count, total);
	}

	public SharedPagination(Page<Long, T> page) {
		super(page);
	}

	@Override
	public Long getNextCursorId() {
		Long next = super.getNextCursorId();
		return next == null ? Pagination.super.getNextCursorId() : next;
	}

	public void setPageNumber(long pageNumber) {
		setCursorId(PageSupport.getStart(pageNumber, getCount()));
	}
}
