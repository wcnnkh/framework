package scw.util.page;

import java.util.NoSuchElementException;

public class EmptyPageables<K, T> extends EmptyPageable<K, T> implements
		Pageables<K, T> {
	private static final long serialVersionUID = 1L;

	public EmptyPageables(K cursorId, long count) {
		super(cursorId, count);
	}

	@Override
	public Pageables<K, T> next() {
		throw new NoSuchElementException();
	}

	@Override
	public Pageable<K, T> process(K start, long count) {
		return new EmptyPageable<>(start, count);
	}
}
