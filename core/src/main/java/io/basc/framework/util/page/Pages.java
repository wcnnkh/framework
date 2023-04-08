package io.basc.framework.util.page;

import io.basc.framework.util.Elements;

public interface Pages<K, T> extends Page<K, T>, Pageables<K, T> {

	@Override
	default Pages<K, T> next() {
		return jumpTo(getNextCursorId());
	}

	default Pages<K, T> jumpTo(K cursorId) {
		return jumpTo(cursorId, getLimit());
	}

	@Override
	default Pages<K, T> shared() {
		return new SharedPages<>(this);
	}

	/**
	 * 获取所有页
	 */
	default Elements<? extends Page<K, T>> pages() {
		return Elements.of(() -> new PageablesIterator<>(this, (e) -> e.next()));
	}

	Pages<K, T> jumpTo(K cursorId, long count);

	@Override
	default Page<K, T> all() {
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
		return new StandardPaginations<>(getTotal(), start, limit,
				(s, count) -> Pages.this.all().stream().skip(start).limit(limit));
	}
}
