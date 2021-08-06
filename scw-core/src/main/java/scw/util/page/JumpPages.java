package scw.util.page;

public class JumpPages<T> extends JumpPageables<Long, T> implements Pages<T> {
	private final long pageNumber;
	private final long total;

	public JumpPages(Page<T> page, CursorProcessor<Long, T> processor) {
		this(page, new PageProcessor<T>(page, processor));
	}

	public JumpPages(Page<T> page, PageableProcessor<Long, T> processor) {
		this(page, processor, page.getPageNumber(), page.getTotal());
	}

	public JumpPages(Pageable<Long, T> pageable,
			PageableProcessor<Long, T> processor, long pageNumber, long total) {
		super(pageable, processor);
		this.pageNumber = pageNumber;
		this.total = total;
	}

	@Override
	public Pages<T> process(Long start, long count) {
		Pageable<Long, T> pageable = super.process(start, count);
		return new JumpPages<T>(pageable, this, PageSupport.getPageNumber(
				count, start), getTotal());
	}

	@Override
	public long getPageNumber() {
		return pageNumber;
	}

	@Override
	public long getPages() {
		return PageSupport.getPages(total, pageNumber);
	}

	@Override
	public long getTotal() {
		return total;
	}
}
