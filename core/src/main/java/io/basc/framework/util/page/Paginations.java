package io.basc.framework.util.page;

import io.basc.framework.util.Elements;

/**
 * 分页操作
 * <p>
 * 默认使用的内存分页实现，如果有更好的实现请重写{@link Paginations#getElements()}和{@link #jumpTo(Long, long)}
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public class Paginations<T> extends Pagination<T> implements Pageable<Long, T> {
	private static final long serialVersionUID = 1L;

	public Paginations(Elements<T> elements) {
		setElements(elements);
	}

	public Paginations(Pagination<T> pagination) {
		super(pagination);
	}

	@Override
	public Paginations<T> clone() {
		return new Paginations<>(this);
	}

	public Paginations<T> previous() {
		return jumpToPage(getPageNumber() - 1);
	}

	public Paginations<T> jumpToPage(long pageNumber) {
		return jumpToPage(pageNumber, getPageSize());
	}

	public Paginations<T> jumpToPage(long pageNumber, long count) {
		return jumpTo(PageSupport.getStart(pageNumber, count), count);
	}

	public Paginations<T> jumpTo(Long cursorId) {
		return jumpTo(cursorId, getPageSize());
	}

	@Override
	public Pagination<T> all() {
		Pagination<T> pagination = new Pagination<>();
		pagination.setTotal(getTotal());
		pagination.setCursorId(0);
		pagination.setPageSize(pagination.getTotal());
		pagination.setElements(pages().flatMap((e) -> e.getElements()));
		return pagination;
	}

	@Override
	public Paginations<T> next() {
		return jumpTo(getNextCursorId());
	}

	@Override
	public Elements<? extends Pagination<T>> pages() {
		return Elements.of(() -> new BrowseableIterator<>(this, (e) -> e.next()));
	}

	@Override
	public Elements<T> getElements() {
		Elements<T> elements = super.getElements();
		if (getCursorId() > 0) {
			elements = elements.skip(getCursorId());
		}

		if (getPageSize() > 0) {
			elements = elements.limit(getPageSize());
		}
		return elements;
	}

	public Paginations<T> jumpTo(Long cursorId, long count) {
		Paginations<T> paginations = clone();
		paginations.setCursorId(cursorId);
		paginations.setPageSize(count);
		return paginations;
	}
}
