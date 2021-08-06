package scw.util.page;

import java.util.List;

public class PageProcessor<T> implements PageableProcessor<Long, T> {
	private final Page<T> page;
	private final CursorProcessor<Long, T> processor;

	public PageProcessor(Page<T> page, CursorProcessor<Long, T> processor) {
		this.page = page;
		this.processor = processor;
	}

	@Override
	public Pageable<Long, T> process(Long start, long count) {
		List<T> list = processor.process(start, count);
		return PageSupport.toPage(page.getTotal(), PageSupport.getPageNumber(count, start), count, list);
	}
}
