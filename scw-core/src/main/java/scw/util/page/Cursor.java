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

	/**
	 * 游标是否已经关闭
	 * 
	 * @return {@code true} if cursor closed.
	 */
	boolean isClosed();

	/**
	 * 关闭游标
	 */
	@Override
	void close();

	@Override
	default Cursor<K, T> jumpTo(PageableProcessor<K, T> processor, K cursorId) {
		Pageable<K, T> pageable = Pageable.super.jumpTo(processor, cursorId);
		return new JumpCursor<>(pageable);
	}

	@Override
	default Cursor<K, T> next(PageableProcessor<K, T> processor) {
		Pageable<K, T> pageable = Pageable.super.next(processor);
		return new JumpCursor<>(pageable);
	}

}
