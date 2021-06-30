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
		this(iterator, new SimpleCursorPosition(0));
	}

	public Cursor(Iterator<T> iterator, CursorPosition cursorPosition) {
		super(StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(new CursorIterator<T>(iterator, cursorPosition), 0), false));
		this.cursorPosition = cursorPosition;
	}

	private Cursor(Stream<T> stream, CursorPosition cursorPosition) {
		super(stream);
		this.cursorPosition = cursorPosition;
	}

	public long getPosition() {
		return cursorPosition.getPosition();
	}

	@Override
	protected Cursor<T> wrapper(Stream<T> stream) {
		if (stream instanceof Cursor) {
			return (Cursor<T>) stream;
		}

		return new Cursor<>(stream, this.cursorPosition);
	}
}
