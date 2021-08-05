package scw.util.page;

import java.util.stream.Stream;

public class PageProcessor<T> implements PageableProcessor<Long, T> {
	private final Page<T> page;
	private final CursorProcessor<Long, T> processor;

	public PageProcessor(Page<T> page, CursorProcessor<Long, T> processor) {
		this.page = page;
		this.processor = processor;
	}

	@Override
	public Pageable<Long, T> process(Long start, long count) {
		Stream<T> stream = processor.stream(start, count);
		return new StreamCursor<>(stream, count, start,
				PageSupport.getNextStart(start, count), PageSupport.hasMore(
						page.getTotal(), count, start));
	}
}
