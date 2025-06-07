package run.soeasy.framework.core.page;

@FunctionalInterface
public interface CursorQuery<K, T> {
	Cursor<K, T> query(K cursorId, long count);
}