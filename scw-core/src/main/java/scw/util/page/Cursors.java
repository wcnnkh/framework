package scw.util.page;

import java.util.NoSuchElementException;
import java.util.function.Function;

public interface Cursors<K, T> extends Cursor<K, T>, Pageables<K, T> {

	@Override
	Cursors<K, T> process(K start, long count);
	
	@Override
	public default <R> Cursors<K, R> map(Function<? super T, ? extends R> mapper) {
		Cursor<K, R> cursor = Cursor.super.map(mapper);
		return new JumpCursors<>(cursor, new MapperPageableProcessor<>(this, mapper));
	}

	default Cursors<K, T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException(
					"cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId() + ", count=" + getCount());
		}
		return jumpTo(getNextCursorId());
	}

	default Cursors<K, T> jumpTo(K cursorId) {
		return jumpTo(this, cursorId);
	}

	default <R> Cursors<K, R> jumpTo(PageableProcessor<K, R> processor, K cursorId) {
		Pageable<K, R> pageable = processor.process(cursorId, getCount());
		return new JumpCursors<>(pageable, processor);
	}

	@Override
	default <R> Cursors<K, R> next(PageableProcessor<K, R> processor) {
		if (!hasNext()) {
			throw new NoSuchElementException(
					"cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId() + ", count=" + getCount());
		}
		return jumpTo(processor, getNextCursorId());
	}
}
