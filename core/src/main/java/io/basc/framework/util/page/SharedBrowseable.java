package io.basc.framework.util.page;

import lombok.NonNull;

public class SharedBrowseable<K, T> extends SharedCursor<K, T> implements Browseable<K, T> {
	private static final long serialVersionUID = 1L;
	private Browseable<K, T> pageables;

	public SharedBrowseable() {
	}

	public SharedBrowseable(@NonNull Browseable<K, T> pageables) {
		this(pageables, pageables);
	}

	public SharedBrowseable(@NonNull Cursor<K, T> currentPage, Browseable<K, T> pageables) {
		super(currentPage);
		this.pageables = pageables;
	}

	@Override
	public Browseable<K, T> jumpTo(K cursorId) {
		Browseable<K, T> pageables = this.pageables.jumpTo(cursorId);
		return new SharedBrowseable<>(pageables);
	}

	@Override
	public Browseable<K, T> shared() {
		return this;
	}

	@Override
	public Browseable<K, T> next() {
		Browseable<K, T> pageables = this.pageables.next();
		return new SharedBrowseable<>(pageables);
	}

	public Browseable<K, T> getPageables() {
		return pageables;
	}

	public void setPageables(Browseable<K, T> pageables) {
		this.pageables = pageables;
	}
}
