package scw.util.page;

import java.util.function.Function;

public interface Pages<T> extends Page<T>, Pageables<Long, T> {

	Pages<T> process(Long start, long count);
	
	default Page<T> shared(){
		return new SharedPage<>(getCursorId(), rows(), getCount(), getTotal());
	}

	@Override
	default boolean hasNext() {
		return Page.super.hasNext();
	}

	@Override
	default <R> Pages<R> map(Function<? super T, ? extends R> mapper) {
		Page<R> page = Page.super.map(mapper);
		return new JumpPages<R>(page, new MapperPageableProcessor<>(this, mapper));
	}

	@Override
	default Pages<T> next() {
		return jumpTo(getNextCursorId());
	}

	@Override
	default <R> Pages<R> jumpTo(PageableProcessor<Long, R> processor,
			Long cursorId) {
		Page<R> page = Page.super.jumpTo(processor, cursorId);
		return new JumpPages<>(page, processor);
	}
	
	@Override
	default <R> Pages<R> jumpToPage(PageableProcessor<Long, R> processor,
			long pageNumber) {
		Page<R> page = Page.super.jumpToPage(processor, pageNumber);
		return new JumpPages<>(page, processor);
	}

	@Override
	default <R> Pages<R> next(PageableProcessor<Long, R> processor) {
		return jumpToPage(processor, getPageNumber() + 1);
	}

	default Pages<T> jumpTo(Long cursorId) {
		return jumpTo(this, cursorId);
	}

	default Pages<T> jumpToPage(long pageNumber) {
		return jumpToPage(this, pageNumber);
	}

	default Pages<T> previous() {
		return jumpToPage(getPageNumber() - 1);
	}
}
