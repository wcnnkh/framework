package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Pages<T> extends Page<T>, Pageables<Long, T> {

	default Page<T> shared(){
		return new SharedPage<>(getCursorId(), rows(), getCount(), getTotal());
	}

	@Override
	default boolean hasNext() {
		return Page.super.hasNext();
	}

	@Override
	default Pages<T> next() {
		return jumpTo(getNextCursorId());
	}

	Pages<T> jumpTo(Long cursorId);

	default Pages<T> jumpToPage(long pageNumber) {
		return jumpTo(PageSupport.getStart(pageNumber, getCount()));
	}

	default Pages<T> previous() {
		return jumpToPage(getPageNumber() - 1);
	}
	
	default Stream<Pages<T>> pages(){
		Iterator<Pages<T>> iterator = new PagesIterator<>(this);
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
	}
}
