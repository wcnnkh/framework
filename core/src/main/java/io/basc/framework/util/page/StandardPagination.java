package io.basc.framework.util.page;

public class StandardPagination<T> extends StandardPage<Long, T> implements Pagination<T> {

	public StandardPagination(long total, long limit) {
		super(total, limit);
	}

	public StandardPagination(Pagination<T> pagination) {
		super(pagination);
	}
}
