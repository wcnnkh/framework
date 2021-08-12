package scw.util.page;

import java.util.function.Function;

public interface Page<T> extends Pageable<Long, T> {
	/**
	 * 总数
	 * 
	 * @return
	 */
	long getTotal();
	
	/**
	 * 获取当前页码
	 * 
	 * @return
	 */
	default long getPageNumber() {
		return PageSupport.getPageNumber(getCount(), getCursorId());
	}

	/**
	 * 总页数
	 * 
	 * @return
	 */
	default long getPages(){
		return PageSupport.getPages(getTotal(), getCount());
	}

	@Override
	default boolean hasNext() {
		return getPageNumber() < getPages();
	}

	default <R> Page<R> next(PageableProcessor<Long, R> processor) {
		return jumpToPage(processor, getPageNumber() + 1);
	}

	default <R> Page<R> jumpTo(PageableProcessor<Long, R> processor,
			Long cursorId) {
		Pageable<Long, R> pageable = processor.process(cursorId, getCount());
		return new JumpPage<>(pageable, PageSupport.getPageNumber(getCount(),
				cursorId), getTotal());
	}

	default <R> Page<R> jumpToPage(PageableProcessor<Long, R> processor,
			long pageNumber) {
		return jumpTo(processor, PageSupport.getStart(pageNumber, getCount()));
	}

	default boolean hasPrevious() {
		return getPageNumber() > 1;
	}

	default <R> Page<R> previous(PageableProcessor<Long, R> processor) {
		return jumpToPage(processor, getPageNumber() - 1);
	}

	@Override
	default Long getNextCursorId() {
		Long start = getCursorId();
		if (start == null) {
			start = 0L;
		}
		return PageSupport.getNextStart(start, getCount());
	}

	@Override
	default <R> Page<R> map(Function<? super T, ? extends R> mapper) {
		return new MapperPage<>(this, mapper);
	}
	
}
