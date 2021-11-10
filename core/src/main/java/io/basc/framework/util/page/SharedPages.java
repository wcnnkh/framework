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
}
