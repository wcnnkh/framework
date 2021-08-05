package scw.util.page;

import java.util.function.Function;

/**
 * 使用游标分页
 * 
 * @author shuchaowen
 *
 * @param <K>
 * @param <T>
 */
public interface Cursor<K, T> extends Pageable<K, T>, AutoCloseable {

	default <R> Cursor<K, R> map(Function<? super T, ? extends R> mapper) {
		return new MapperCursor<>(this, mapper);
	}

	void close();
	
	@Override
	default Cursor<K, T> shared() {
		return new SharedCursor<K, T>(getCursorId(), rows(), getNextCursorId(), getCount(), hasNext());
	}

	@Override
	default <R> Cursor<K, R> jumpTo(PageableProcessor<K, R> processor, K cursorId) {
		Pageable<K, R> pageable = Pageable.super.jumpTo(processor, cursorId);
		return new JumpCursor<>(pageable);
	}

	@Override
	default <R> Cursor<K, R> next(PageableProcessor<K, R> processor) {
		Pageable<K, R> pageable = Pageable.super.next(processor);
		return new JumpCursor<>(pageable);
	}

}
