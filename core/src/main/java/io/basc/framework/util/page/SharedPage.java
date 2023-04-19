package io.basc.framework.util.page;

import java.util.List;

import io.basc.framework.lang.Nullable;

public class SharedPage<K, T> extends SharedPageable<K, T> implements Page<K, T> {
	private static final long serialVersionUID = 1L;
	private long total;
	private long pageSize;

	/**
	 * 默认的构造方法
	 * 
	 */
	public SharedPage() {
	}

	public SharedPage(K cursorId) {
		super(cursorId);
	}

	public SharedPage(K cursorId, long pageSize) {
		this(cursorId, null, pageSize, 0);
	}

	public SharedPage(K cursorId, List<T> rows, long pageSize, long total) {
		this(cursorId, rows, null, pageSize, total);
	}

	public SharedPage(K cursorId, List<T> rows, @Nullable K nextCursorId, long pageSize, long total) {
		super(cursorId, rows, nextCursorId);
		this.pageSize = pageSize;
		this.total = total;
	}

	public SharedPage(Page<K, T> page) {
		super(page);
		this.pageSize = page.getPageSize();
		this.total = page.getTotal();
	}

	@Override
	public long getTotal() {
		return total;
	}

	@Override
	public void setList(List<T> list) {
		if (total == 0) {
			total = list.size();
		}
		super.setList(list);
	}

	public void setTotal(long total) {
		this.total = total;
	}

	@Override
	public long getPageSize() {
		return pageSize;
	}

	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

}
