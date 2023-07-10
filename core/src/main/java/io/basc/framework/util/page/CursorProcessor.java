package io.basc.framework.util.page;

@FunctionalInterface
public interface CursorProcessor<K, T> {
	Cursor<K, T> process(K cursorId, long count);
}