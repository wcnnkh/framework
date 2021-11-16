package io.basc.framework.util.page;

import java.util.List;

import io.basc.framework.lang.Nullable;

public class SharedPage<T> extends SharedPageable<Long, T> implements Page<T> {
	private static final long serialVersionUID = 1L;
	private long total;

	/**
	 * 默认的构造方法
	 * 
	 * @see SharedPageable#DEFAULT_PAGE_SIZE
	 */
	public SharedPage() {
		this(DEFAULT_PAGE_SIZE);
	}

	/**
	 * @param count 每页的数量
	 */
	public SharedPage(long count) {
		super(0L, count);
	}

	/**
	 * @param cursorId
	 * @param rows
	 * @param count    每页的数量
	 * @param total
	 */
	public SharedPage(long cursorId, List<T> rows, long count, long total) {
		this(cursorId, rows, null, count, total);
	}

	/**
	 * @param cursorId
	 * @param rows
	 * @param nextCursorId 如果为空自动计算下一页的起始点{@link Page#getNextCursorId()}
	 * @param count        每页的数量
	 * @param total
	 * @param hasMore
	 */
	public SharedPage(long cursorId, List<T> rows, @Nullable Long nextCursorId, long count, long total) {
		super(cursorId, rows, nextCursorId, count);
		this.total = total;
	}

	public SharedPage(Page<T> page) {
		super(page);
		this.total = page.getTotal();
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
	public Long getNextCursorId() {
		Long next = super.getNextCursorId();
		return next == null ? Page.super.getNextCursorId() : next;
	}

	public void setPageNumber(long pageNumber) {
		setCursorId(PageSupport.getStart(pageNumber, getCount()));
	}
	
	@Override
	public void setRows(List<T> rows) {
		if(total == 0) {
			total = rows.size();
		}
		super.setRows(rows);
	}

	public void setTotal(long total) {
		this.total = total;
	}
}
