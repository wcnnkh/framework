package io.basc.framework.util.page;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;

public class SharedPages<K, T> extends SharedPage<K, T> implements Pages<K, T> {
	private static final long serialVersionUID = 1L;
	private Pages<K, T> pages;

	public SharedPages() {
	}

	public SharedPages(Pages<K, T> pages) {
		this(pages, pages);
	}

	public SharedPages(Page<K, T> currentPage, @Nullable Pages<K, T> pages) {
		super(currentPage);
		this.pages = pages;
	}

	@Override
	public Pages<K, T> jumpTo(K cursorId, long count) {
		Pages<K, T> pages = this.pages.jumpTo(cursorId, count);
		return new SharedPages<>(pages);
	}

	@Override
	public Pages<K, T> shared() {
		return this;
	}

	@Override
	public Pages<K, T> next() {
		Pages<K, T> pages = this.pages.next();
		return new SharedPages<>(pages);
	}

	@Override
	public Elements<? extends Page<K, T>> pages() {
		return pages.pages();
	}

	public Pages<K, T> getPages() {
		return pages;
	}

	public void setPages(Pages<K, T> pages) {
		this.pages = pages;
	}
}
