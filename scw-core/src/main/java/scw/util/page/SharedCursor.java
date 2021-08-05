package scw.util.page;

import java.util.List;
import java.util.stream.Stream;

public class SharedCursor<K, T> extends SharedPageable<K, T> implements
		Cursor<K, T> {
	private static final long serialVersionUID = 1L;

	public SharedCursor(K cursorId, List<T> list, K nextCursorId,
			Long count, boolean hasNext) {
		super(cursorId, list, nextCursorId, count, hasNext);
	}
	
	@Override
	public Stream<T> stream() {
		return super.stream().onClose(() -> close());
	}

	@Override
	public void close() {
	}
}
