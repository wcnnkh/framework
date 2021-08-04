package scw.util.page;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import scw.core.Assert;
import scw.event.Event;
import scw.lang.Nullable;

public interface Pageable<K, T> extends Iterable<T>, Event {
	/**
	 * 获取当前页的使用的开始游标
	 * 
	 * @return
	 */
	@Nullable
	K getCursorId();

	/**
	 * 分页的限制数据(limit)
	 * 
	 * @return
	 */
	Long getCount();

	/**
	 * 获取下一页的开始游标id
	 * 
	 * @return
	 */
	@Nullable
	K getNextCursorId();

	/**
	 * 是否还有更多数据
	 * 
	 * @return
	 */
	boolean hasNext();

	default <R> Pageable<K, R> map(Function<? super T, ? extends R> mapper) {
		return new MapperPageable<>(this, mapper);
	}

	default Stream<T> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	default List<T> rows() {
		return stream().collect(Collectors.toList());
	}

	default Pageable<K, T> jumpTo(PageableProcessor<K, T> processor, K cursorId) {
		Assert.requiredArgument(processor != null, "processor");
		return processor.process(cursorId, getCount());
	}

	default Pageable<K, T> next(PageableProcessor<K, T> processor) {
		if (!hasNext()) {
			throw new NoSuchElementException(
					"cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId() + ", count=" + getCount());
		}
		return jumpTo(processor, getNextCursorId());
	}
}