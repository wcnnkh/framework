package io.basc.framework.util.page;

import io.basc.framework.util.XUtils;
import io.basc.framework.util.stream.StreamProcessorSupport;

import java.util.Iterator;
import java.util.stream.Stream;

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

	@Override
	default Page<K, T> all() {
		return new StreamPage<K, T>(getCursorId(), () -> streamAll(), null, getTotal(), getTotal());
	}

	Pages<K, T> jumpTo(K cursorId, long count);

	/**
	 * 这是极端情况下的处理，不推荐使用(性能低下)
	 * 
	 * @param start
	 * @param limit
	 * @return
	 */
	default Paginations<T> toPaginations(long start, long limit) {
		return new StreamPaginations<T>(getTotal(), start, limit,
				(s, count) -> StreamProcessorSupport.cursor(Pages.this.streamAll()).limit(s, count));
	}
}
