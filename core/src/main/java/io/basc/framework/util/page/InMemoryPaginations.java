package io.basc.framework.util.page;

import java.io.Serializable;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

/**
 * 在内存中分页
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public class InMemoryPaginations<T> implements Paginations<T>, Serializable {
	private static final long serialVersionUID = 1L;
	private final Elements<T> elements;
	private long cursorId;
	private long limit;

	public InMemoryPaginations(Elements<T> elements) {
		Assert.requiredArgument(elements != null, "elements");
		this.elements = elements;
	}

	public void setCursorId(long cursorId) {
		Assert.isTrue(cursorId >= 0, "cursorId[" + cursorId + "] cannot be less than 0");
		this.cursorId = cursorId;
	}

	public void setLimit(long limit) {
		Assert.isTrue(limit > 0, "limit[" + limit + "] greater than 0 is required");
		this.limit = limit;
	}

	@Override
	public long getTotal() {
		return elements.count();
	}

	@Override
	public long getLimit() {
		return limit > 0 ? limit : getTotal();
	}

	@Override
	public Long getCursorId() {
		return cursorId;
	}

	@Override
	public Long getNextCursorId() {
		long end = cursorId + limit;
		long total = getTotal();
		if (end <= 0 || end >= total) {
			return null;
		}
		return Math.min(end, total);
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
		Assert.requiredArgument(cursorId != null, "cursorId");
		InMemoryPaginations<T> inMemoryPaginations = new InMemoryPaginations<T>(this.elements);
		inMemoryPaginations.setCursorId(cursorId);
		inMemoryPaginations.setLimit(count);
		return inMemoryPaginations;
	}
}
