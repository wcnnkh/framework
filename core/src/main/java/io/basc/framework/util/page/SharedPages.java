package io.basc.framework.util.page;

public class SharedPages<T> extends SharedPage<T> implements Pages<T> {
	private static final long serialVersionUID = 1L;
	private final transient Pages<T> pages;

	public SharedPages(Pages<T> pages) {
		super(pages);
		this.pages = pages;
	}

	@Override
	public Pages<T> jumpTo(Long cursorId, long count) {
		Pages<T> pages = this.pages.jumpTo(cursorId, count);
		return new SharedPages<>(pages);
	}

	@Override
	public Pages<T> shared() {
		return this;
	}

	@Override
	public Pages<T> jumpTo(Long cursorId) {
		Pages<T> pages = this.pages.jumpTo(cursorId);
		return new SharedPages<>(pages);
	}

	@Override
	public Pages<T> next() {
		Pages<T> pages = this.pages.next();
		return new SharedPages<>(pages);
	}

	@Override
	public Pages<T> jumpToPage(long pageNumber) {
		Pages<T> pages = this.pages.jumpToPage(pageNumber);
		return new SharedPages<>(pages);
	}

	@Override
	public Pages<T> jumpToPage(long pageNumber, long count) {
		Pages<T> pages = this.pages.jumpToPage(pageNumber, count);
		return new SharedPages<>(pages);
	}

	@Override
	public Pages<T> previous() {
		Pages<T> pages = this.pages.previous();
		return new SharedPages<>(pages);
	}

	@Override
	public Pages<T> limit(long maxPageNumber) {
		Pages<T> pages = this.pages.limit(maxPageNumber);
		return new SharedPages<>(pages);
	}
}
