package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.CollectionUtils;

public interface Pageable<K, T> extends Iterable<T> {
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

	List<T> getList();

	default Stream<T> stream() {
		return getList().stream();
	}

	/**
	 * 是否还有更多数据
	 * 
	 * @return
	 */
	default boolean hasNext() {
		return getNextCursorId() != null;
	}

	/**
	 * 获取当前分页的第一条数据
	 * 
	 * @return
	 */
	default T first() {
		List<T> rows = getList();
		if (CollectionUtils.isEmpty(rows)) {
			return null;
		}
		return rows.get(0);
	}

	/**
	 * 获取当前分页的最后一条数据
	 * 
	 * @return
	 */
	default T last() {
		List<T> rows = getList();
		if (CollectionUtils.isEmpty(rows)) {
			return null;
		}
		return rows.get(rows.size() - 1);
	}

	@Override
	default Iterator<T> iterator() {
		return stream().iterator();
	}

	default Pageable<K, T> shared() {
		return new SharedPageable<>(this);
	}
}