package io.basc.framework.data.domain;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.page.InMemoryPaginations;
import io.basc.framework.util.page.Paginations;

public class Query<T> extends InMemoryPaginations<T> {
	private long limit;
	private long cursorId;
	private final Elements<T> elements;

	public Query(Elements<T> elements) {
		Assert.requiredArgument(elements != null, "elements");
		this.elements = elements;
		PageRequest request = PageRequest.getPageRequest();
		if (request != null) {
			this.cursorId = request.getStart();
			this.limit = request.getPageSize();
		}
	}

	@Override
	public Long getCursorId() {
		return cursorId;
	}

	public void setCursorId(long cursorId) {
		this.cursorId = cursorId;
	}

	@Override
	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	@Override
	public long getTotal() {
		return getElements().count();
	}

	@Override
	public Elements<T> getElements() {
		Elements<T> elements = this.elements;
		if (cursorId > 0) {
			elements = elements.convert((e) -> e.skip(cursorId));
		}

		if (limit > 0) {
			elements = elements.convert((e) -> e.limit(limit));
		}
		return elements;
	}

	@Override
	public Paginations<T> jumpTo(Long cursorId, long count) {
		Assert.requiredArgument(cursorId != null && cursorId > 0, "cursorId");
		return new InMemoryPaginations<>(this.elements, cursorId, count);
	}
}
