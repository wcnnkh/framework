package io.basc.framework.util.page;

import java.util.function.Function;

public class PaginationsWrapper<T, W extends Paginations<T>> extends PagesWrapper<Long, T, W>
		implements Paginations<T> {

	public PaginationsWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Paginations<T> shared() {
		return wrappedTarget.shared();
	}

	@Override
	public Paginations<T> jumpTo(Long cursorId, long count) {
		return wrappedTarget.jumpTo(cursorId, count);
	}

	@Override
	public Paginations<T> jumpTo(Long cursorId) {
		return wrappedTarget.jumpTo(cursorId);
	}

	@Override
	public Paginations<T> jumpToPage(long pageNumber) {
		return wrappedTarget.jumpToPage(pageNumber);
	}

	@Override
	public Paginations<T> jumpToPage(long pageNumber, long count) {
		return wrappedTarget.jumpToPage(pageNumber, count);
	}

	@Override
	public <TT> Pagination<TT> map(Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(valueMapper);
	}

	@Override
	public Pagination<T> all() {
		return wrappedTarget.all();
	}
}
