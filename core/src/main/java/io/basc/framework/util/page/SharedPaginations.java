package io.basc.framework.util.page;

import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;

public class SharedPaginations<T> extends SharedPagination<T> implements Paginations<T> {
	private static final long serialVersionUID = 1L;
	private Paginations<T> paginations;

	public SharedPaginations() {
	}

	public SharedPaginations(Paginations<T> paginations) {
		this(paginations, paginations);
	}

	public SharedPaginations(Page<Long, T> currentPage, @Nullable Paginations<T> paginations) {
		super(currentPage);
		this.paginations = paginations;
	}

	@Override
	public Paginations<T> jumpTo(Long cursorId, long count) {
		Paginations<T> pages = this.paginations.jumpTo(cursorId);
		if (pages instanceof SharedPaginations) {
			return pages;
		}
		return new SharedPaginations<>(pages);
	}

	@Override
	public Paginations<T> shared() {
		return this;
	}

	@Override
	public Paginations<T> jumpToPage(long pageNumber) {
		Paginations<T> pages = this.paginations.jumpToPage(pageNumber);
		if (pages instanceof SharedPaginations) {
			return pages;
		}
		return new SharedPaginations<>(pages);
	}

	@Override
	public Paginations<T> jumpToPage(long pageNumber, long count) {
		Paginations<T> pages = this.paginations.jumpToPage(pageNumber, count);
		if (pages instanceof SharedPaginations) {
			return pages;
		}
		return new SharedPaginations<>(pages);
	}

	@Override
	public Paginations<T> previous() {
		Paginations<T> pages = this.paginations.previous();
		if (pages instanceof SharedPaginations) {
			return pages;
		}
		return new SharedPaginations<>(pages);
	}

	@Override
	public Paginations<T> limit(long maxPageNumber) {
		Paginations<T> pages = this.paginations.limit(maxPageNumber);
		if (pages instanceof SharedPaginations) {
			return pages;
		}
		return new SharedPaginations<>(pages);
	}

	@Override
	public Stream<? extends Pages<Long, T>> pages() {
		return paginations.pages();
	}

	public Paginations<T> getPaginations() {
		return paginations;
	}

	public void setPaginations(Paginations<T> paginations) {
		this.paginations = paginations;
	}
}
