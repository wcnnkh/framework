package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import io.basc.framework.util.CollectionUtils;

public class AllPageable<S extends Pageables<K, T>, K, T> implements Pageable<K, T> {
	protected final S source;

	public AllPageable(S source) {
		this.source = source;
	}

	@Override
	public K getCursorId() {
		return source.getCursorId();
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
	public Iterator<T> iterator() {
		return CollectionUtils.iterator(source.pages().iterator(), (e) -> e.iterator());
	}

	@Override
	public final List<T> getList() {
		return toList();
	}

	@Override
	public Stream<T> stream() {
		return source.pages().stream().flatMap((e) -> e.stream());
	}
}
