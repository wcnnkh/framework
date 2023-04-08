package io.basc.framework.util.page;

import io.basc.framework.util.Elements;

public class PageablesWrapper<K, T, W extends Pageables<K, T>> extends PageableWrapper<K, T, W>
		implements Pageables<K, T> {

	public PageablesWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Pageables<K, T> shared() {
		return wrappedTarget.shared();
	}

	@Override
	public Pageables<K, T> jumpTo(K cursorId) {
		return wrappedTarget.jumpTo(cursorId);
	}

	@Override
	public Pageables<K, T> next() {
		return wrappedTarget.next();
	}

	@Override
	public Pageable<K, T> all() {
		return wrappedTarget.all();
	}

	@Override
	public Elements<? extends Pageable<K, T>> pages() {
		return wrappedTarget.pages();
	}

}
