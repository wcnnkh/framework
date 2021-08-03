package scw.util.page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import scw.lang.Nullable;
import scw.util.stream.AutoCloseStream;
import scw.util.stream.StreamProcessorSupport;

public interface Cursor<K, T> extends Iterable<T>, AutoCloseable {
	/**
	 * 获取当前页的使用的开始游标
	 * 
	 * @return
	 */
	@Nullable
	K getCursorId();

	/**
	 * 分页的限制数据
	 * 
	 * @return
	 */
	Long getCount();

	<R> Cursor<K, R> map(Function<? super T, ? extends R> mapper);

	/**
	 * 获取下一页的开始游标id
	 * 
	 * @return
	 */
	@Nullable
	K getNextCursorId();

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

	default AutoCloseStream<T> stream() {
		Stream<T> stream = StreamSupport.stream(spliterator(), false);
		stream = stream.onClose(() -> close());
		return StreamProcessorSupport.autoClose(stream);
	}

	default List<T> rows() {
		return stream().collect(Collectors.toList());
	}
}
