package io.basc.framework.util.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import io.basc.framework.mapper.MapperUtils;

public class SharedPageable<K, T> implements Pageable<K, T>, Serializable {
	private static final long serialVersionUID = 1L;

	private List<T> list;
	private K cursorId;
	private K nextCursorId;

	/**
	 * 默认的构造方法，cursorId为空
	 */
	public SharedPageable() {
	}

	/**
	 * @param cursorId
	 * @param count 每页的数量
	 */
	public SharedPageable(K cursorId) {
		this(cursorId, Collections.emptyList(), null);
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
	public List<T> getList() {
		if(list == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(list);
	}

	@Override
	public K getCursorId() {
		return cursorId;
	}

	@Override
	public K getNextCursorId() {
		return nextCursorId;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public void setCursorId(K cursorId) {
		this.cursorId = cursorId;
	}

	public void setNextCursorId(K nextCursorId) {
		this.nextCursorId = nextCursorId;
	}

	@Override
	public String toString() {
		return MapperUtils.toString(this);
	}
}
