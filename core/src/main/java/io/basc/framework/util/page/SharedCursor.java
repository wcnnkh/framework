package io.basc.framework.util.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import io.basc.framework.util.ElementList;
import io.basc.framework.util.Elements;

public class SharedCursor<K, T> extends StandardCursor<K, T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 默认的构造方法，cursorId为空
	 */
	public SharedCursor() {
	}

	public SharedCursor(K cursorId) {
		this(cursorId, Collections.emptyList(), null);
	}

	public SharedCursor(K cursorId, List<T> list, K nextCursorId) {
		super(cursorId, new ElementList<>(list), nextCursorId);
	}

	public SharedCursor(Cursor<K, T> pageable) {
		super(pageable.getCursorId(), pageable.getElements().toList(), pageable.getNextCursorId());
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
