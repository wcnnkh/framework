package io.basc.framework.util.page;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import io.basc.framework.util.XUtils;

public interface Pageables<K, T> extends Pageable<K, T> {
	Pageables<K, T> jumpTo(K cursorId);
	
	default Pageables<K, T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException("cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId());
		}
		return jumpTo(getNextCursorId());
	}

	default Stream<? extends Pageables<K, T>> pages() {
		return XUtils.stream(new PageablesIterator<>(this, (e) -> e.next()));
	}

	@Override
	default Pageables<K, T> shared() {
		return new SharedPageables<>(this);
	}

	default Pageable<K, T> all() {
		return new AllPageable<>(this);
	}
}