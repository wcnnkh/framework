package io.basc.framework.util.page;

public class StreamPaginations<T> extends StreamPages<Long, T> implements Paginations<T> {

	public StreamPaginations(Page<Long, T> page, CursorProcessor<Long, T> processor) {
		super(page, (start, limit) -> {
			return new StreamPageable<>(start, () -> processor.process(start, limit),
					PageSupport.hasMore(page.getTotal(), start, limit) ? PageSupport.getNextStart(start, limit) : null);
		});
	}

	private StreamPaginations(long total, Long cursorId, long count, PageableProcessor<Long, T> processor) {
		super(total, cursorId, count, processor);
	}

	public StreamPaginations(long total, Long cursorId, long count, CursorProcessor<Long, T> processor) {
		this(new StreamPage<>(cursorId, () -> processor.process(cursorId, count),
				PageSupport.hasMore(total, cursorId, count) ? PageSupport.getNextStart(cursorId, count) : null, count,
				total), processor);
	}

	@Override
	public Paginations<T> jumpTo(Long cursorId, long count) {
		return new StreamPaginations<>(getTotal(), cursorId, count, processor);
	}
}
