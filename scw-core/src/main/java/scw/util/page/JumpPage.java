package scw.util.page;

public class JumpPage<T> extends PageableWrapper<Pageable<Long, T>, Long, T> implements Page<T> {
	private final long pageNumber;
	private final long total;

	public JumpPage(Pageable<Long, T> pageable, long pageNumber, long total) {
		super(pageable);
		this.pageNumber = pageNumber;
		this.total = total;
	}

	@Override
	public long getPageNumber() {
		return pageNumber;
	}

	@Override
	public long getPages() {
		return PageSupport.getPages(total, wrappedTarget.getCount());
	}

	@Override
	public long getTotal() {
		return total;
	}
}
