package io.basc.framework.util.page;

import java.util.NoSuchElementException;

public class EmptyPages<K, T> extends SharedPage<K, T> implements Pages<K, T> {
	private static final long serialVersionUID = 1L;

	@Override
	public Pages<K, T> next() {
		throw new NoSuchElementException("next");
	}

	@Override
	public Pages<K, T> jumpTo(K cursorId, long count) {
		throw new NoSuchElementException("jumpTo cursorId[" + cursorId + "] count[" + count + "]");
	}
}
