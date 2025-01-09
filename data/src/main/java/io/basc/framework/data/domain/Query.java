package io.basc.framework.data.domain;

import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.page.Pagination;
import io.basc.framework.util.page.Paginations;

public class Query<T> extends Paginations<T> {
	private static final long serialVersionUID = 1L;

	public Query(Elements<T> elements) {
		super(elements);
		PageRequest request = PageRequest.getPageRequest();
		if (request != null) {
			setCursorId(request.getStart());
			setPageSize(request.getPageSize());
		}
	}

	public Query(Pagination<T> pagination) {
		super(pagination);
	}

	@Override
	public Query<T> clone() {
		return new Query<>(this);
	}
}
