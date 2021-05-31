package scw.redis.connection;

import scw.lang.Nullable;

public abstract class KeyBoundCursor<K, T> extends ScanCursor<K, T> {

	private K key;

	/**
	 * Crates new {@link ScanCursor}
	 *
	 * @param cursorId
	 * @param options Defaulted to {@link ScanOptions#NONE} if nulled.
	 */
	public KeyBoundCursor(K key, long cursorId, @Nullable ScanOptions<K> options) {
		super(cursorId, options);
		this.key = key;
	}

	protected ScanIteration<T> doScan(long cursorId, ScanOptions<K> options) {
		return doScan(this.key, cursorId, options);
	}

	protected abstract ScanIteration<T> doScan(K key, long cursorId, ScanOptions<K> options);

	public K getKey() {
		return key;
	}

}
