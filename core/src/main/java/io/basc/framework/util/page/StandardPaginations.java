package io.basc.framework.util.page;

import java.util.function.Function;

public class StandardPaginations<T> extends StandardPages<Long, T> implements Paginations<T> {

	public StandardPaginations(Page<Long, T> page, Pager<Long, T> processor) {
		super(page, new PaginationProcessor<>(page.getTotal(), processor));
	}

	private StandardPaginations(long total, Long cursorId, long count, PageableProcessor<Long, T> processor) {
		super(total, cursorId, count, processor);
	}

	public StandardPaginations(long total, Long cursorId, long count, Pager<Long, T> processor) {
		this(total, cursorId, count, new PaginationProcessor<>(total, processor));
	}

	@Override
	public Paginations<T> jumpTo(Long cursorId, long count) {
		return new StandardPaginations<>(getTotal(), cursorId, count, getProcessor());
	}

	@Override
	public Paginations<T> shared() {
		return Paginations.super.shared();
	}

	@Override
	public <TT> Pagination<TT> map(Function<? super T, ? extends TT> valueMapper) {
		return Paginations.super.map(valueMapper);
	}
}
