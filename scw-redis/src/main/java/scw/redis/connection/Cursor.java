package scw.redis.connection;

import scw.data.domain.CloseableIterator;

public interface Cursor<T> extends CloseableIterator<T> {

	/**
	 * Get the reference cursor. <br>
	 * <strong>NOTE:</strong> the id might change while iterating items.
	 *
	 * @return
	 */
	long getCursorId();

	/**
	 * @return {@code true} if cursor closed.
	 */
	boolean isClosed();

	/**
	 * @return the current position of the cursor.
	 */
	long getPosition();
}