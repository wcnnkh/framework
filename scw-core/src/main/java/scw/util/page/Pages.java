package scw.util.page;

public interface Pages<T> extends Page<T>, Cursors<Long, T> {
	@Override
	default Pages<T> next() {
		return jumpTo(getNextCursorId());
	}

	@Override
	default Pages<T> jumpTo(PageableProcessor<Long, T> processor, Long cursorId) {
		Page<T> page = Page.super.jumpTo(processor, cursorId);
		return new JumpPages<>(processor, page, PageSupport.getPageNumber(getCount(), cursorId), getTotal());
	}

	@Override
	default Pages<T> jumpToPage(PageableProcessor<Long, T> processor, long pageNumber) {
		Page<T> page = Page.super.jumpToPage(processor, pageNumber);
		return new JumpPages<>(processor, page, pageNumber, getTotal());
	}

	@Override
	default Pages<T> next(PageableProcessor<Long, T> processor) {
		return jumpToPage(processor, getPageNumber() + 1);
	}

	default Pages<T> jumpTo(Long cursorId) {
		return jumpTo(getProcessor(), cursorId);
	}

	default Pages<T> jumpToPage(long pageNumber) {
		return jumpToPage(getProcessor(), pageNumber);
	}

	default Pages<T> previous() {
		return jumpToPage(getPageNumber() - 1);
	}
}
