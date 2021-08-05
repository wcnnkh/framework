package scw.util.page;

import java.util.NoSuchElementException;

public class EmptyPages<T> extends EmptyCursors<Long, T> implements Pages<T> {
	private static final long serialVersionUID = 1L;

	public EmptyPages(Long cursorId, long count) {
		super(cursorId, count);
	}

	public Pages<T> process(Long start, long count) {
		return new EmptyPages<T>(start, count);
	}

	@Override
	public long getTotal() {
		return 0L;
	}

	@Override
	public Pages<T> next() {
		throw new NoSuchElementException();
	}
}
