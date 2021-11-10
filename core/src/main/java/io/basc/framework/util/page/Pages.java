package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.util.XUtils;

public interface Pages<T> extends Page<T>, Pageables<Long, T> {

	@Override
	default Pages<T> next() {
		return jumpTo(getNextCursorId());
	}

	default Pages<T> jumpTo(Long cursorId){
		return jumpTo(cursorId, getCount());
	}
	
	@Override
	default Pages<T> shared() {
		return new SharedPages<>(this);
	}
	
	Pages<T> jumpTo(Long cursorId, long count);
	
	default Pages<T> jumpToPage(long pageNumber) {
		return jumpToPage(pageNumber, getCount());
	}
	
	default Pages<T> jumpToPage(long pageNumber, long count) {
		return jumpTo(PageSupport.getStart(pageNumber, count), count);
	}

	default Pages<T> previous() {
		return jumpToPage(getPageNumber() - 1);
	}
	
	default Stream<? extends Pages<T>> pages(){
		Iterator<Pages<T>> iterator = new PagesIterator<>(this);
		return XUtils.stream(iterator);
	}
	
	default Pages<T> limit(long maxPageNumber){
		return new StreamPages<>(Math.min(getTotal(), PageSupport.getStart(maxPageNumber, getCount())), getCursorId(), getCount(), (start, limit) -> jumpTo(start).stream());
	}
}
