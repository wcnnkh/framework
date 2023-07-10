package io.basc.framework.util.page;

import io.basc.framework.lang.Nullable;

public class SharedBrowsable<K, T> extends SharedCursor<K, T> implements Browsable<K, T> {
	private static final long serialVersionUID = 1L;
	private Browsable<K, T> pageables;

	public SharedBrowsable() {
	}

	public SharedBrowsable(Browsable<K, T> pageables) {
		this(pageables, pageables);
	}

	public SharedBrowsable(Cursor<K, T> currentPage, @Nullable Browsable<K, T> pageables) {
		super(currentPage);
		this.pageables = pageables;
	}

	@Override
	public Browsable<K, T> jumpTo(K cursorId) {
		Browsable<K, T> pageables = this.pageables.jumpTo(cursorId);
		return new SharedBrowsable<>(pageables);
	}

	@Override
	public Browsable<K, T> shared() {
		return this;
	}

	@Override
	public Browsable<K, T> next() {
		Browsable<K, T> pageables = this.pageables.next();
		return new SharedBrowsable<>(pageables);
	}

	public Browsable<K, T> getPageables() {
		return pageables;
	}

	public void setPageables(Browsable<K, T> pageables) {
		this.pageables = pageables;
	}
}
