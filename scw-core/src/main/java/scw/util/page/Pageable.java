package scw.util.page;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import scw.core.Assert;
import scw.lang.Nullable;

public interface Pageable<K, T> extends Iterable<T>{
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
	long getCount();

	/**
	 * 获取下一页的开始游标id
	 * 
	 * @return
	 */
	@Nullable
	K getNextCursorId();
	
	List<T> rows();

	/**
	 * 是否还有更多数据
	 * 
	 * @return
	 */
	boolean hasNext();
	
	@Override
	default Iterator<T> iterator() {
		return rows().iterator();
	}
	
	default <R> Pageable<K, R> map(Function<? super T, ? extends R> mapper) {
		return new MapperPageable<>(this, mapper);
	}

	default <R> Pageable<K, R> jumpTo(PageableProcessor<K, R> processor,
			K cursorId) {
		Assert.requiredArgument(processor != null, "processor");
		return processor.process(cursorId, getCount());
	}

	default <R> Pageable<K, R> next(PageableProcessor<K, R> processor) {
		if (!hasNext()) {
			throw new NoSuchElementException("cursorId=" + getCursorId()
					+ ", nextCursorId=" + getNextCursorId() + ", count="
					+ getCount());
		}
		return jumpTo(processor, getNextCursorId());
	}
}