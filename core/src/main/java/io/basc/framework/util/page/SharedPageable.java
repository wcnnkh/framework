package io.basc.framework.util.page;

import io.basc.framework.util.Elements;
import lombok.NonNull;

public class SharedPageable<K, T> extends SharedPage<K, T> implements Pageable<K, T> {
	private static final long serialVersionUID = 1L;
	private Pageable<K, T> pages;

	public SharedPageable() {
	}

	public SharedPageable(@NonNull Pageable<K, T> pages) {
		this(pages, pages);
	}

	public SharedPageable(@NonNull Page<K, T> currentPage, Pageable<K, T> pages) {
		super(currentPage);
		this.pages = pages;
	}

	@Override
	public Pageable<K, T> jumpTo(K cursorId, long count) {
		Pageable<K, T> pages = this.pages.jumpTo(cursorId, count);
		return new SharedPageable<>(pages);
	}

	@Override
	public Pageable<K, T> shared() {
		return this;
	}

	@Override
	public Pageable<K, T> next() {
		Pageable<K, T> pages = this.pages.next();
		return new SharedPageable<>(pages);
	}

	@Override
	public Elements<? extends Page<K, T>> pages() {
		return pages.pages();
	}

	public Pageable<K, T> getPages() {
		return pages;
	}

	public void setPages(Pageable<K, T> pages) {
		this.pages = pages;
	}
}
