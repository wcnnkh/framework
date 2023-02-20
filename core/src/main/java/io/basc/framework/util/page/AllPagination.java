package io.basc.framework.util.page;

public class AllPagination<S extends Paginations<T>, T> extends AllPage<S, Long, T> implements Pagination<T> {

	public AllPagination(S source) {
		super(source);
	}

	@Override
	public long getPages() {
		return 1;
	}

	@Override
	public long getPageNumber() {
		return 1;
	}
}
