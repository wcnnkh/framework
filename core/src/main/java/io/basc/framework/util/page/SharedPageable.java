package io.basc.framework.util.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class SharedPageable<K, T> implements Pageable<K, T>, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 默认的分页数量
	 */
	public static final long DEFAULT_PAGE_SIZE = 10;

	private List<T> rows;
	private K cursorId;
	private K nextCursorId;
	private long count;
	private boolean next;

	/**
	 * 默认的构造方法，cursorId为空
	 * 
	 * @see SharedPageable#DEFAULT_PAGE_SIZE
	 */
	public SharedPageable() {
		this(null, DEFAULT_PAGE_SIZE);
	}

	/**
	 * @param cursorId
	 * @param count 每页的数量
	 */
	public SharedPageable(K cursorId, long count) {
		this(cursorId, Collections.emptyList(), null, count, false);
	}

	/**
	 * @param cursorId
	 * @param rows
	 * @param nextCursorId
	 * @param count 每页的数量
	 * @param next
	 */
	public SharedPageable(K cursorId, List<T> rows, K nextCursorId, long count, boolean next) {
		this.cursorId = cursorId;
		this.nextCursorId = nextCursorId;
		this.rows = rows;
		this.count = count;
		this.next = next;
	}

	@Override
	public List<T> rows() {
		return Collections.unmodifiableList(rows);
	}

	@Override
	public K getCursorId() {
		return cursorId;
	}

	@Override
	public long getCount() {
		return count;
	}

	@Override
	public K getNextCursorId() {
		return nextCursorId;
	}

	@Override
	public boolean hasNext() {
		return next;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public boolean isNext() {
		return next;
	}

	public void setNext(boolean next) {
		this.next = next;
	}

	public void setCursorId(K cursorId) {
		this.cursorId = cursorId;
	}

	public void setNextCursorId(K nextCursorId) {
		this.nextCursorId = nextCursorId;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
