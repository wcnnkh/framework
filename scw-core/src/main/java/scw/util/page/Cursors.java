package scw.util.page;

import java.util.NoSuchElementException;

public interface Cursors<K, T> extends Cursor<K, T>, Pageables<K, T> {

	default Cursors<K, T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException(
					"cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId() + ", count=" + getCount());
		}
		return jumpTo(getNextCursorId());
	}

	default Cursors<K, T> jumpTo(K cursorId) {
		return jumpTo(getProcessor(), cursorId);
	}

	default Cursors<K, T> jumpTo(PageableProcessor<K, T> processor, K cursorId) {
		Pageable<K, T> pageable = processor.process(cursorId, getCount());
		return new JumpCursors<>(processor, pageable);
	}

	@Override
	default Cursors<K, T> next(PageableProcessor<K, T> processor) {
		if (!hasNext()) {
			throw new NoSuchElementException(
					"cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId() + ", count=" + getCount());
		}
		return jumpTo(processor, getNextCursorId());
	}
}
