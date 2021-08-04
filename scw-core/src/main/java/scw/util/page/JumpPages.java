package scw.util.page;

public class JumpPages<T> extends JumpCursors<Long, T> implements Pages<T>{
	private final long pageNumber;
	private final long total;
	
	public JumpPages(PageableProcessor<Long, T> processor, Pageable<Long, T> pageable, long pageNumber, long total) {
		super(processor, pageable);
		this.pageNumber = pageNumber;
		this.total = total;
	}

	@Override
	public Long getPageNumber() {
		return pageNumber;
	}

	@Override
	public Long getPages() {
		return PageSupport.getPages(total, pageNumber);
	}

	@Override
	public Long getTotal() {
		return total;
	}

	@Override
	public Pages<T> jumpTo(Long cursorId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pages<T> jumpToPage(long pageNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pages<T> previous() {
		// TODO Auto-generated method stub
		return null;
	}

}
