package scw.util.page;

public class SimplePage<T> extends SimpleCursor<Long, T> implements Page<T> {
	private static final long serialVersionUID = 1L;
	private Long total;

	public SimplePage(long cursorId, Iterable<T> iterable, long count, long total) {
		this(cursorId, iterable, PageSupport.getNextStart(cursorId, count, total), count, total,
				PageSupport.hasMore(total, count, cursorId));
	}

	public SimplePage(long cursorId, Iterable<T> iterable, long nextCursorId, long count, long total,
			boolean hasMore) {
		super(cursorId, iterable, nextCursorId, count, hasMore);
		this.total = total;
	}

	@Override
	public Long getCursorId() {
		return Math.min(super.getCursorId(), total);
	}

	@Override
	public Long getPages() {
		return PageSupport.getPages(getTotal(), getCount());
	}

	@Override
	public Long getTotal() {
		return total;
	}

	@Override
	public Long getPageNumber() {
		return PageSupport.getPageNumber(getCount(), getCursorId());
	}

}
