package io.basc.framework.util.page;

import java.util.NoSuchElementException;

public class EmptyPages<T> extends SharedPage<T> implements Pages<T> {
	private static final long serialVersionUID = 1L;

	@Override
	public Pages<T> next() {
		throw new NoSuchElementException("next");
	}

	@Override
	public Pages<T> jumpTo(Long cursorId, long count) {
		throw new NoSuchElementException("jumpTo cursorId[" + cursorId + "] count[" + count + "]");
	}
}
