package io.basc.framework.util.page;

import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;

import io.basc.framework.util.ObjectUtils;

public class ListPageables<K, T> implements Pageables<K, T>, Serializable {
	private static final long serialVersionUID = 1L;
	private final List<? extends Pageable<K, T>> pageables;
	private final int index;

	public ListPageables(List<? extends Pageable<K, T>> pageables) {
		this(pageables, 0);
	}

	public ListPageables(List<? extends Pageable<K, T>> pageables, int index) {
		this.pageables = pageables;
		this.index = index;
	}

	@Override
	public K getCursorId() {
		return pageables.get(index).getCursorId();
	}

	@Override
	public K getNextCursorId() {
		if (index + 1 >= pageables.size()) {
			// 不存在
			return null;
		}

		return pageables.get(index + 1).getCursorId();
	}

	@Override
	public boolean hasNext() {
		return index + 1 < pageables.size();
	}

	@Override
	public List<T> getList() {
		return pageables.get(index).getList();
	}

	@Override
	public Pageables<K, T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException("cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId());
		}
		return new ListPageables<>(pageables, index + 1);
	}

	@Override
	public Pageables<K, T> jumpTo(K cursorId) {
		for (int i = 0; i < pageables.size(); i++) {
			if (ObjectUtils.equals(pageables.get(i).getCursorId(), cursorId)) {
				return new ListPageables<>(pageables, i);
			}
		}
		throw new NoSuchElementException(String.valueOf(cursorId));
	}

}
