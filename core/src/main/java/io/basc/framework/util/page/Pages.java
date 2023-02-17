package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.util.Cursor;
import io.basc.framework.util.XUtils;

public interface Pages<K, T> extends Page<K, T>, Pageables<K, T> {

	@Override
	default Pages<K, T> next() {
		return jumpTo(getNextCursorId());
	}

	default Pages<K, T> jumpTo(K cursorId) {
		return jumpTo(cursorId, getCount());
	}

	@Override
	default Pages<K, T> shared() {
		return new SharedPages<>(this);
	}

	default Stream<? extends Pages<K, T>> pages() {
		Iterator<Pages<K, T>> iterator = new PagesIterator<>(this);
		return XUtils.stream(iterator);
	}

	Pages<K, T> jumpTo(K cursorId, long count);

	@Override
	default Pageable<K, T> all() {
		return new AllPage<>(this);
	}

	/**
	 * 这是极端情况下的处理，不推荐使用(性能低下)
	 * 
	 * @param start
	 * @param limit
	 * @return
	 */
	default Paginations<T> toPaginations(long start, long limit) {
		return new StreamPaginations<>(getTotal(), start, limit,
				(s, count) -> Cursor.of(Pages.this.all()).limit(s, count));
	}
}
