package scw.util.page;

import java.util.Iterator;

import scw.util.Wrapper;

public class PageableWrapper<P extends Pageable<K, T>, K, T> extends Wrapper<P> implements Pageable<K, T> {

	public PageableWrapper(P wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Iterator<T> iterator() {
		return wrappedTarget.iterator();
	}

	@Override
	public K getCursorId() {
		return wrappedTarget.getCursorId();
	}

	@Override
	public long getCount() {
		return wrappedTarget.getCount();
	}

	@Override
	public K getNextCursorId() {
		return wrappedTarget.getNextCursorId();
	}

	@Override
	public boolean hasNext() {
		return wrappedTarget.hasNext();
	}
}
