package scw.redis.connection;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import scw.core.utils.CollectionUtils;
import scw.lang.Nullable;

public abstract class ScanCursor<K, T> implements Cursor<T> {
	private CursorState state;
	private long cursorId;
	private Iterator<T> delegate;
	private final ScanOptions<K> scanOptions;
	private long position;

	/**
	 * Crates new {@link ScanCursor}
	 *
	 * @param cursorId the cursor Id.
	 * @param options  Defaulted to {@link ScanOptions#NONE} if {@code null}.
	 */
	public ScanCursor(long cursorId, @Nullable ScanOptions<K> options) {
		this.scanOptions = options;
		this.cursorId = cursorId;
		this.state = CursorState.READY;
		this.delegate = Collections.emptyIterator();
	}

	private void scan(long cursorId) {
		ScanIteration<T> result = doScan(cursorId, this.scanOptions);
		processScanResult(result);
	}

	/**
	 * Performs the actual scan command using the native client implementation. The
	 * given {@literal options} are never {@code null}.
	 *
	 * @param cursorId
	 * @param options
	 * @return
	 */
	protected abstract ScanIteration<T> doScan(long cursorId, ScanOptions<K> options);

	/**
	 * Initialize the {@link Cursor} prior to usage.
	 */
	public final ScanCursor<K, T> open() {
		if (!isReady()) {
			throw new IllegalStateException("Cursor already " + state + ". Cannot (re)open it.");
		}

		state = CursorState.OPEN;
		doOpen(cursorId);
		return this;
	}

	/**
	 * Customization hook when calling {@link #open()}.
	 *
	 * @param cursorId
	 */
	protected void doOpen(long cursorId) {
		scan(cursorId);
	}

	private void processScanResult(ScanIteration<T> result) {
		cursorId = result.getCursorId();
		if (isFinished(cursorId)) {
			state = CursorState.FINISHED;
		}

		if (!CollectionUtils.isEmpty(result.getItems())) {
			delegate = result.iterator();
		} else {
			resetDelegate();
		}
	}

	/**
	 * Check whether {@code cursorId} is finished.
	 *
	 * @param cursorId the cursor Id
	 * @return {@literal true} if the cursor is considered finished,
	 *         {@literal false} otherwise.s
	 */
	protected boolean isFinished(long cursorId) {
		return cursorId == 0;
	}

	private void resetDelegate() {
		delegate = Collections.emptyIterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.core.Cursor#getCursorId()
	 */
	@Override
	public long getCursorId() {
		return cursorId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {

		assertCursorIsOpen();

		while (!delegate.hasNext() && !CursorState.FINISHED.equals(state)) {
			scan(cursorId);
		}

		if (delegate.hasNext()) {
			return true;
		}

		return cursorId > 0;
	}

	private void assertCursorIsOpen() {
		if (isReady() || isClosed()) {
			throw new IllegalStateException("Cannot access closed cursor. Did you forget to call open()?");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		assertCursorIsOpen();

		if (!hasNext()) {
			throw new NoSuchElementException("No more elements available for cursor " + cursorId + ".");
		}

		T next = moveNext(delegate);
		position++;

		return next;
	}

	/**
	 * Fetch the next item from the underlying {@link Iterable}.
	 *
	 * @param source
	 * @return
	 */
	protected T moveNext(Iterator<T> source) {
		return source.next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove is not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public final void close() {

		try {
			doClose();
		} finally {
			state = CursorState.CLOSED;
		}
	}

	/**
	 * Customization hook for cleaning up resources on when calling
	 * {@link #close()}.
	 */
	protected void doClose() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.core.Cursor#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return state == CursorState.CLOSED;
	}

	protected final boolean isReady() {
		return state == CursorState.READY;
	}

	protected final boolean isOpen() {
		return state == CursorState.OPEN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.core.Cursor#getPosition()
	 */
	@Override
	public long getPosition() {
		return position;
	}

	/**
	 * @author Thomas Darimont
	 */
	enum CursorState {
		READY, OPEN, FINISHED, CLOSED;
	}
}
