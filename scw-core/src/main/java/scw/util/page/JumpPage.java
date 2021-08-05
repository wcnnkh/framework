package scw.util.page;

import java.util.Iterator;

public class JumpPage<T> implements Page<T> {
	private final Pageable<Long, T> pageable;
	private final long pageNumber;
	private final long total;

	public JumpPage(Pageable<Long, T> pageable, long pageNumber, long total) {
		this.pageable = pageable;
		this.pageNumber = pageNumber;
		this.total = total;
	}

	@Override
	public void close() {
		if (pageable instanceof Cursor) {
			((Cursor<?, ?>) pageable).close();
		}
	}

	@Override
	public Long getCursorId() {
		return pageable.getCursorId();
	}

	@Override
	public long getCount() {
		return pageable.getCount();
	}

	@Override
	public boolean hasNext() {
		return pageable.hasNext();
	}

	@Override
	public Iterator<T> iterator() {
		return pageable.iterator();
	}

	@Override
	public long getPageNumber() {
		return pageNumber;
	}

	@Override
	public long getPages() {
		return PageSupport.getPages(total, pageable.getCount());
	}

	@Override
	public long getTotal() {
		return total;
	}
}
