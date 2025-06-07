package run.soeasy.framework.core.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.ListElementsWrapper;
import run.soeasy.framework.core.collection.StandardListElements;

public class SharedCursor<K, T> extends CustomizeCursor<K, T> implements Serializable {
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
		super(cursorId, new StandardListElements<>(list), nextCursorId);
	}

	public SharedCursor(@NonNull Cursor<K, T> cursor) {
		super(cursor.getCursorId(), cursor.getElements().toList(), cursor.getNextCursorId());
	}

	@Override
	public ListElementsWrapper<T, ?> getElements() {
		return (ListElementsWrapper<T, ?>) super.getElements();
	}

	@Override
	public final void setElements(Elements<T> elements) {
		setList(elements.toList());
	}

	public void setList(List<T> list) {
		super.setElements(new StandardListElements<>(list));
	}
}
