package io.basc.framework.util.page;

import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;

public class SharedPageables<K, T> extends SharedPageable<K, T> implements Pageables<K, T> {
	private static final long serialVersionUID = 1L;
	private Pageables<K, T> pageables;
	
	public SharedPageables() {
	}

	public SharedPageables(Pageables<K, T> pageables) {
		this(pageables, pageables);
	}

	public SharedPageables(Pageable<K, T> currentPage, @Nullable Pageables<K, T> pageables) {
		super(currentPage);
		this.pageables = pageables;
	}

	@Override
	public Pageables<K, T> jumpTo(K cursorId) {
		Pageables<K, T> pageables = this.pageables.jumpTo(cursorId);
		return new SharedPageables<>(pageables);
	}

	@Override
	public Pageables<K, T> shared() {
		return this;
	}

	@Override
	public Pageables<K, T> next() {
		Pageables<K, T> pageables = this.pageables.next();
		return new SharedPageables<>(pageables);
	}
	
	@Override
	public Stream<? extends Pageables<K, T>> pages() {
		return pageables.pages();
	}
	
	public Pageables<K, T> getPageables() {
		return pageables;
	}
	
	public void setPageables(Pageables<K, T> pageables) {
		this.pageables = pageables;
	}
}
