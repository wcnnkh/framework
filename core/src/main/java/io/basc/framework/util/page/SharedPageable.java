package io.basc.framework.util.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import io.basc.framework.util.ElementList;
import io.basc.framework.util.Elements;

public class SharedPageable<K, T> extends StandardPageable<K, T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 默认的构造方法，cursorId为空
	 */
	public SharedPageable() {
	}

	public SharedPageable(K cursorId) {
		this(cursorId, Collections.emptyList(), null);
	}

	public SharedPageable(K cursorId, List<T> list, K nextCursorId) {
		super(cursorId, new ElementList<>(list), nextCursorId);
	}

	public SharedPageable(Pageable<K, T> pageable) {
		this(pageable.getCursorId(), pageable.getList(), pageable.getNextCursorId());
	}

	@Override
	public ElementList<T> getElements() {
		return (ElementList<T>) super.getElements();
	}

	@Override
	public final void setElements(Elements<T> elements) {
		setList(elements.toList());
	}

	public void setList(List<T> list) {
		super.setElements(new ElementList<>(list));
	}
}
