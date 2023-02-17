package io.basc.framework.util.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Cursor;

public class SharedPageable<K, T> implements Pageable<K, T>, Serializable {
	private static final long serialVersionUID = 1L;
	private K cursorId;
	private K nextCursorId;
	private List<T> list;

	/**
	 * 默认的构造方法，cursorId为空
	 */
	public SharedPageable() {
	}

	/**
	 * @param cursorId
	 * @param count    每页的数量
	 */
	public SharedPageable(K cursorId) {
		this(cursorId, Collections.emptyList(), null);
	}

	protected Iterator<? extends T> getIterator() {
		return Collections.emptyIterator();
	}

	/**
	 * @param cursorId
	 * @param list
	 * @param nextCursorId
	 * @param next
	 */
	public SharedPageable(K cursorId, List<T> list, K nextCursorId) {
		this.cursorId = cursorId;
		this.nextCursorId = nextCursorId;
		this.list = list;
	}

	public SharedPageable(Pageable<K, T> pageable) {
		this(pageable.getCursorId(), pageable.getList(), pageable.getNextCursorId());
	}

	@Override
	public K getCursorId() {
		return cursorId;
	}

	@Override
	public K getNextCursorId() {
		return nextCursorId;
	}

	public void setCursorId(K cursorId) {
		this.cursorId = cursorId;
	}

	public void setNextCursorId(K nextCursorId) {
		this.nextCursorId = nextCursorId;
	}

	public List<T> getList() {
		return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}

	@Override
	public Cursor<T> iterator() {
		return Cursor.of(list);
	}
}
