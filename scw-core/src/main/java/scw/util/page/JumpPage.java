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
	public boolean isClosed() {
		if (pageable instanceof Cursor) {
			return ((Cursor<?, ?>) pageable).isClosed();
		}
		return false;
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
	public Long getCount() {
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
	public long getCreateTime() {
		return pageable.getCreateTime();
	}

	@Override
	public Long getPageNumber() {
		return pageNumber;
	}

	@Override
	public Long getPages() {
		return PageSupport.getPages(total, pageable.getCount());
	}

	@Override
	public Long getTotal() {
		return total;
	}
}
