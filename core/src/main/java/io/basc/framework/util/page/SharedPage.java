package io.basc.framework.util.page;

import java.util.List;

import io.basc.framework.lang.Nullable;

public class SharedPage<K, T> extends SharedPageable<K, T> implements Page<K, T> {
	private static final long serialVersionUID = 1L;
	private long total;
	private long limit;

	/**
	 * 默认的构造方法
	 * 
	 */
	public SharedPage() {
	}

	public SharedPage(K cursorId) {
		super(cursorId);
	}

	public SharedPage(K cursorId, long limit) {
		this(cursorId, null, limit, 0);
	}

	public SharedPage(K cursorId, List<T> rows, long limit, long total) {
		this(cursorId, rows, null, limit, total);
	}

	public SharedPage(K cursorId, List<T> rows, @Nullable K nextCursorId, long limit, long total) {
		super(cursorId, rows, nextCursorId);
		this.limit = limit;
		this.total = total;
	}

	public SharedPage(Page<K, T> page) {
		super(page);
		this.limit = page.getLimit();
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
	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

}
