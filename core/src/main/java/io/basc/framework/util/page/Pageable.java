package io.basc.framework.util.page;

import java.util.function.Function;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;

public interface Pageable<K, T> {
	/**
	 * 获取当前页的使用的开始游标
	 * 
	 * @return
	 */
	@Nullable
	K getCursorId();

	/**
	 * 获取下一页的开始游标id
	 * 
	 * @return
	 */
	@Nullable
	K getNextCursorId();

	Elements<T> getElements();

	/**
	 * 是否还有更多数据
	 * 
	 * @return
	 */
	default boolean hasNext() {
		return getNextCursorId() != null;
	}

	default Pageable<K, T> shared() {
		return new SharedPageable<>(this);
	}

	default <TT> Pageable<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return map(Function.identity(), valueMapper);
	}

	default <TK, TT> Pageable<TK, TT> map(Function<? super K, ? extends TK> keyMapper,
			Function<? super T, ? extends TT> valueMapper) {
		return new ConvertiblePageable<>(this, keyMapper, valueMapper);
	}
}