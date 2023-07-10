package io.basc.framework.util.page;

import io.basc.framework.util.Elements;

public class AllCursor<S extends Browsable<K, T>, K, T> implements Cursor<K, T> {
	protected final S source;

	public AllCursor(S source) {
		this.source = source;
	}

	@Override
	public K getCursorId() {
		return source.getCursorId();
	}

	@Override
	public K getNextCursorId() {
		return null;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public Elements<T> getElements() {
		return source.pages().flatMap((e) -> e.getElements());
	}
}
