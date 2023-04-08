package io.basc.framework.util.page;

import java.util.function.Function;

public interface Pagination<T> extends Page<Long, T> {
	/**
	 * 获取当前页码
	 * 
	 * @return
	 */
	default long getPageNumber() {
		return PageSupport.getPageNumber(getCursorId(), getLimit());
	}

	/**
	 * 总页数
	 * 
	 * @return
	 */
	default long getPages() {
		return PageSupport.getPages(getTotal(), getLimit());
	}

	default boolean hasPrevious() {
		return getPageNumber() > 1;
	}

	@Override
	default Long getNextCursorId() {
		Long start = getCursorId();
		if (start == null) {
			return null;
		}

		if (!PageSupport.hasMore(getTotal(), start, getLimit())) {
			return null;
		}

		return PageSupport.getNextStart(start, getLimit());
	}

	@Override
	default Pagination<T> shared() {
		return new SharedPagination<>(this);
	}

	@Override
	default <TT> Pagination<TT> map(Function<? super T, ? extends TT> map) {
		return new ConvertiblePagination<>(this, map);
	}
}
