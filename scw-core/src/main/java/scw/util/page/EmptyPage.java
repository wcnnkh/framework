package scw.util.page;

public class EmptyPage<T> extends EmptyCursor<Long, T> implements Page<T> {
	private static final long serialVersionUID = 1L;

	public EmptyPage(Long cursorId, Long count) {
		super(cursorId, count);
	}

	@Override
	public long getPageNumber() {
		return PageSupport.getPageNumber(getCount(), getCursorId());
	}

	@Override
	public long getPages() {
		return 1L;
	}

	@Override
	public long getTotal() {
		return 0L;
	}

}
