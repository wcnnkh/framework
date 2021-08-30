package io.basc.framework.redis.core.convert;

import io.basc.framework.convert.Converter;
import io.basc.framework.redis.core.Cursor;

public class ConvertibleCursor<S, T> implements Cursor<T> {
	private final Cursor<S> cursor;
	private final Converter<S, T> converter;

	public ConvertibleCursor(Cursor<S> cursor, Converter<S, T> converter) {
		this.cursor = cursor;
		this.converter = converter;
	}

	@Override
	public void close() {
		cursor.close();
	}

	@Override
	public boolean hasNext() {
		return cursor.hasNext();
	}

	@Override
	public T next() {
		S value = cursor.next();
		return converter.convert(value);
	}

	@Override
	public long getCursorId() {
		return cursor.getCursorId();
	}

	@Override
	public boolean isClosed() {
		return cursor.isClosed();
	}

	@Override
	public long getPosition() {
		return cursor.getPosition();
	}

}
