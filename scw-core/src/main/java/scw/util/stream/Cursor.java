package scw.util.stream;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 游标,只可被迭代一次
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public final class Cursor<T> extends AbstractAutoCloseStreamWrapper<T, Cursor<T>> implements StreamPosition {
	private final CursorPosition cursorPosition;

	public Cursor(Iterator<T> iterator) {
		this(iterator, 0);
	}

	public Cursor(Stream<T> stream) {
		this(stream, 0);
	}

	public Cursor(Stream<T> stream, long position) {
		this(stream, new SimpleCursorPosition(position));
	}

	public Cursor(Iterator<T> iterator, long position) {
		this(iterator, new SimpleCursorPosition(position));
	}

	private Cursor(Iterator<T> iterator, CursorPosition cursorPosition) {
		super(stream(iterator, cursorPosition));
		this.cursorPosition = cursorPosition;
	}

	private Cursor(Stream<T> stream, CursorPosition cursorPosition) {
		super(stream(stream.iterator(), cursorPosition).onClose(() -> stream.close()));
		this.cursorPosition = cursorPosition;
	}

	public long getPosition() {
		return cursorPosition.getPosition();
	}

	@Override
	protected Cursor<T> wrapper(Stream<T> stream) {
		return new Cursor<>(stream);
	}

	private static <E> Stream<E> stream(Iterator<E> iterator, CursorPosition cursorPosition) {
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(new CursorIterator<E>(iterator, cursorPosition), 0), false);
	}
}
