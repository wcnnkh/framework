package run.soeasy.framework.util.page;

import run.soeasy.framework.util.collections.Elements;

public class AllCursor<S extends Browseable<K, T>, K, T> implements Cursor<K, T> {
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
