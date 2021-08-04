package scw.util.page;

import java.util.function.Function;

public interface Page<T> extends Cursor<Long, T> {
	/**
	 * 获取当前页码
	 * 
	 * @return
	 */
	Long getPageNumber();

	/**
	 * 总页数
	 * 
	 * @return
	 */
	Long getPages();

	/**
	 * 总数
	 * 
	 * @return
	 */
	Long getTotal();

	default Page<T> next(PageableProcessor<Long, T> processor) {
		return jumpToPage(processor, getPageNumber() + 1);
	}

	default Page<T> jumpTo(PageableProcessor<Long, T> processor, Long cursorId) {
		Pageable<Long, T> pageable = processor.process(cursorId, getCount());
		return new JumpPage<>(pageable, PageSupport.getPageNumber(getCount(), cursorId), getTotal());
	}

	default Page<T> jumpToPage(PageableProcessor<Long, T> processor, long pageNumber) {
		return jumpTo(processor, PageSupport.getStart(pageNumber, getCount()));
	}

	default boolean hasPrevious() {
		return getPageNumber() > 1;
	}

	default Page<T> previous(PageableProcessor<Long, T> processor) {
		return jumpToPage(processor, getPageNumber() - 1);
	}

	@Override
	default Long getNextCursorId() {
		Long start = getCursorId();
		if (start == null) {
			start = 0L;
		}
		return PageSupport.getNextStart(start, getCount(), getTotal());
	}

	@Override
	default <R> Page<R> map(Function<? super T, ? extends R> mapper) {
		return new MapperPage<>(this, mapper);
	}
}
