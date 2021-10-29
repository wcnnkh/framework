package io.basc.framework.util.stream;

import io.basc.framework.util.XUtils;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 游标
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public final class Cursor<T> extends StreamMapWrapper<T, Cursor<T>> implements StreamPosition {
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
		initWrap(stream);
		this.cursorPosition = cursorPosition;
	}
	
	@Override
	public <R> Cursor<R> map(Function<? super T, ? extends R> mapper) {
		Stream<R> stream = super.map(mapper);
		return new Cursor<R>(stream, cursorPosition);
	}

	public long getPosition() {
		return cursorPosition.getPosition();
	}

	@Override
	protected Cursor<T> wrap(Stream<T> stream) {
		if(stream instanceof Cursor){
			return (Cursor<T>) stream;
		}
		return new Cursor<>(stream);
	}
	
	private static <E> Stream<E> stream(Iterator<E> iterator, CursorPosition cursorPosition) {
		return XUtils.stream(new CursorIterator<E>(iterator, cursorPosition));
	}
}
