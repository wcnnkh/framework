package io.basc.framework.util.page;

import io.basc.framework.util.Wrapper;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;

public class PageableWrapper<P extends Pageable<K, T>, K, T> extends Wrapper<P> implements Pageable<K, T> {

	public PageableWrapper(P wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Iterator<T> iterator() {
		return wrappedTarget.iterator();
	}

	@Override
	public K getCursorId() {
		return wrappedTarget.getCursorId();
	}

	@Override
	public K getNextCursorId() {
		return wrappedTarget.getNextCursorId();
	}

	@Override
	public boolean hasNext() {
		return wrappedTarget.hasNext();
	}

	@Override
	public List<T> rows() {
		return wrappedTarget.rows();
	}

	@Override
	public Stream<T> stream() {
		return wrappedTarget.stream();
	}

	@Override
	public T first() {
		return wrappedTarget.first();
	}

	@Override
	public T last() {
		return wrappedTarget.last();
	}

	@Override
	public Spliterator<T> spliterator() {
		return wrappedTarget.spliterator();
	}
}
