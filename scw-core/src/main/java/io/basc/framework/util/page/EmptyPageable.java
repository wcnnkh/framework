package io.basc.framework.util.page;

import io.basc.framework.util.stream.StreamProcessorSupport;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class EmptyPageable<K, T> implements Pageable<K, T>, Serializable {
	private static final long serialVersionUID = 1L;
	private final K cursorId;
	private final long count;

	public EmptyPageable(K cursorId, long count) {
		this.cursorId = cursorId;
		this.count = count;
	}

	@Override
	public K getCursorId() {
		return cursorId;
	}

	@Override
	public long getCount() {
		return count;
	}

	@Override
	public K getNextCursorId() {
		return null;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public List<T> rows() {
		return Collections.emptyList();
	}

	@Override
	public Stream<T> stream() {
		return StreamProcessorSupport.emptyStream();
	}

}
