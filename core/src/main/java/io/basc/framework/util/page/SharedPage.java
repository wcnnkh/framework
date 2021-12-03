package io.basc.framework.util.page;

import java.util.List;

import io.basc.framework.lang.Nullable;

public class SharedPage<K, T> extends SharedPageable<K, T> implements Page<K, T> {
	private static final long serialVersionUID = 1L;
	private long total;
	private long count;

	/**
	 * 默认的构造方法
	 * 
	 */
	public SharedPage() {
	}

	/**
	 * @param count 每页的数量
	 */
	public SharedPage(K cursorId) {
		super(cursorId);
	}
	
	public SharedPage(K cursorId, long count) {
		this(cursorId, null, count, 0);
	}

	/**
	 * @param cursorId
	 * @param rows
	 * @param count    每页的数量
	 * @param total
	 */
	public SharedPage(K cursorId, List<T> rows, long count, long total) {
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
	public SharedPage(K cursorId, List<T> rows, @Nullable K nextCursorId, long count, long total) {
		super(cursorId, rows, nextCursorId);
		this.count = count;
		this.total = total;
	}

	public SharedPage(Page<K, T> page) {
		super(page);
		this.count = page.getCount();
		this.total = page.getTotal();
	}

	@Override
	public long getTotal() {
		return total;
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

	@Override
	public long getCount() {
		return count;
	}
	
	public void setCount(long count) {
		this.count = count; 
	}
}
