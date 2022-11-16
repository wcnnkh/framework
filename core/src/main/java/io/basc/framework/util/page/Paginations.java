package io.basc.framework.util.page;

public interface Paginations<T> extends Pagination<T>, Pages<Long, T> {

	default Paginations<T> previous() {
		return jumpToPage(getPageNumber() - 1);
	}

	default Paginations<T> jumpToPage(long pageNumber) {
		return jumpToPage(pageNumber, getCount());
	}

	default Paginations<T> jumpToPage(long pageNumber, long count) {
		return jumpTo(PageSupport.getStart(pageNumber, count), count);
	}

	default Paginations<T> limit(long maxPageNumber) {
		return new StreamPaginations<>(getTotal(),
				Math.min(getTotal(), PageSupport.getStart(maxPageNumber, getCount())), getCount(),
				(k, c) -> jumpTo(k, c).iterator());
	}

	default Paginations<T> jumpTo(Long cursorId) {
		return jumpTo(cursorId, getCount());
	}

	@Override
	default Paginations<T> shared() {
		return new SharedPaginations<T>(this);
	}

	@Override
	default Pagination<T> all() {
		return new AllPagination<>(this);
	}

	Paginations<T> jumpTo(Long cursorId, long count);
}
