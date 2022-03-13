package io.basc.framework.util.stream;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

import io.basc.framework.util.LimitIterator;
import io.basc.framework.util.XUtils;

/**
 * 游标<br/>
 * 默认都是自动关闭的
 * 
 * @author shuchaowen
 * @see #isAutoClose()
 * @see #close()
 * @see #onClose(Runnable)
 * @param <T>
 */
public class Cursor<T> extends StreamMapWrapper<T, Cursor<T>> implements StreamPosition {
	private final CursorPosition cursorPosition;

	public Cursor(Iterator<T> iterator) {
		this(iterator, 0);
	}

	public Cursor(Iterator<T> iterator, long position) {
		this(iterator, position, AUTO_CLOSE);
	}

	public Cursor(Iterator<T> iterator, boolean autoClose) {
		this(iterator, 0, autoClose);
	}

	public Cursor(Iterator<T> iterator, long position, boolean autoClose) {
		this(XUtils.stream(iterator), position);
		setAutoClose(autoClose);
	}

	public Cursor(Stream<T> stream) {
		this(stream, 0);
	}

	public Cursor(Stream<T> stream, long position) {
		this(stream, new ParallelCursorPosition(position));
	}

	private Cursor(Stream<T> stream, CursorPosition cursorPosition) {
		super(convert(stream, cursorPosition));
		initWrap(stream);
		this.cursorPosition = cursorPosition;
	}

	private static <E> Stream<E> convert(Stream<E> stream, CursorPosition cursorPosition) {
		return XUtils.stream(new CursorIterator<>(stream.iterator(), cursorPosition)).onClose(() -> stream.close());
	}

	@Override
	public <R> Cursor<R> map(Function<? super T, ? extends R> mapper) {
		Stream<R> stream = super.map(mapper);
		return new Cursor<R>(stream, cursorPosition);
	}

	public long getPosition() {
		return cursorPosition.getPosition();
	}

	/**
	 * 限制
	 * 
	 * @param start
	 * @param limit 小于0代表不限制
	 * @return
	 */
	public Cursor<T> limit(long start, long limit) {
		return new Cursor<T>(new LimitIterator<T>(iterator(), start, limit));
	}

	@Override
	protected Cursor<T> wrap(Stream<T> stream) {
		if (stream instanceof Cursor) {
			return (Cursor<T>) stream;
		}
		return new Cursor<>(stream);
	}
}
