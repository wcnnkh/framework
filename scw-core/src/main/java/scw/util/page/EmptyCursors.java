package scw.util.page;

import java.util.NoSuchElementException;

public class EmptyCursors<K, T> extends EmptyPageables<K, T> implements
		Cursors<K, T> {
	private static final long serialVersionUID = 1L;

	public EmptyCursors(K cursorId, Long count) {
		super(cursorId, count);
	}

	@Override
	public Cursors<K, T> process(K start, long count) {
		return new EmptyCursors<>(start, count);
	}

	@Override
	public void close() {
	}

	@Override
	public Cursors<K, T> next() {
		throw new NoSuchElementException();
	}
}
