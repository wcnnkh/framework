package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.util.XUtils;

public interface Pages<T> extends Page<T>, Pageables<Long, T> {

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
		return XUtils.stream(iterator);
	}
}
