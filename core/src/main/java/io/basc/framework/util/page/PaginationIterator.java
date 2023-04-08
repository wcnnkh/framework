package io.basc.framework.util.page;

public class PaginationIterator<T> extends PageIterator<Long, T> {

	public PaginationIterator(Paginations<T> pageables) {
		super(pageables);
	}

	@Override
	public Pagination<T> next() {
		return (Pagination<T>) super.next();
	}
}
