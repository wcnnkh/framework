package io.basc.framework.util.page;

import io.basc.framework.util.Cursor;

@FunctionalInterface
public interface CursorProcessor<K, T> {
	Cursor<T> process(K start, long count);
}
