package io.basc.framework.util.page;

import java.util.List;
import java.util.function.Function;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.ResultSet;

public interface Pageable<K, T> extends ResultSet<T> {
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

	@Override
	Cursor<T> iterator();

	default List<T> getList() {
		return iterator().toList();
	}

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

	default <TT> Pageable<K, TT> map(Function<? super T, ? extends TT> map) {
		return map(Function.identity(), map);
	}

	default <TK, TT> Pageable<TK, TT> map(Function<? super K, ? extends TK> keyMap,
			Function<? super T, ? extends TT> valueMap) {
		return new MapPageable<>(this, keyMap, valueMap);
	}
}