package io.basc.framework.util.page;

import java.util.NoSuchElementException;

import io.basc.framework.util.Elements;

public interface Pageables<K, T> extends Pageable<K, T> {
	Pageables<K, T> jumpTo(K cursorId);

	default Pageables<K, T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException("cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId());
		}
		return jumpTo(getNextCursorId());
	}

	@Override
	default Pageables<K, T> shared() {
		return new SharedPageables<>(this);
	}

	default Elements<? extends Pageable<K, T>> pages() {
		return Elements.of(() -> new PageablesIterator<>(this, (e) -> e.next()));
	}

	default Pageable<K, T> all() {
		return new AllPageable<>(this);
	}
}