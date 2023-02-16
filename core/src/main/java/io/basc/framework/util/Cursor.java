package io.basc.framework.util;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Cursor<E> extends CloseableIterator<E>, Closeable<RuntimeException> {

	BigInteger getPosition();

	Cursor<E> onClose(RunnableProcessor<? extends RuntimeException> close);

	@Override
	default Cursor<E> limit(long start) {
		return limit(BigInteger.valueOf(start), null);
	}

	@Override
	default Cursor<E> limit(long start, long count) {
		return limit(BigInteger.valueOf(start), BigInteger.valueOf(count));
	}

	default <U> Cursor<U> flatConvert(Function<? super Stream<E>, ? extends Stream<U>> mapper) {
		return Cursor.create(mapper.apply(stream()));
	}

	@Override
	default <U> Cursor<U> convert(Function<? super E, ? extends U> converter) {
		return new ConvertibleCursor<>(this, converter, getPosition(), () -> close());
	}

	default <T> Cursor<T> map(Function<? super E, ? extends T> mapper) {
		Cursor<T> cursor = new StandardCursor<>(new ConvertibleIterator<E, T>(this, mapper), getPosition());
		return cursor.onClose(() -> close());
	}

	default Cursor<E> filter(Predicate<? super E> predicate) {
		return new FilterableCursor<>(this, predicate, () -> close());
	}

	/**
	 * 限制
	 * 
	 * @param start
	 * @param limit
	 * @return
	 */
	default Cursor<E> limit(BigInteger start, BigInteger limit) {
		return new FilterableCursor<>(this, start, limit, () -> close());
	}

	@Override
	default Cursor<E> limit(Predicate<? super E> start, Predicate<? super E> end) {
		return new FilterableCursor<>(this, start, end, () -> close());
	}

	@Override
	default void forEachRemaining(Consumer<? super E> action) {
		try {
			CloseableIterator.super.forEachRemaining(action);
		} finally {
			close();
		}
	}

	static <T> Cursor<T> empty() {
		return create(Collections.emptyIterator());
	}

	static <T> Cursor<T> create(Iterator<? extends T> iterator) {
		return new StandardCursor<>(iterator);
	}

	static <T> Cursor<T> create(CloseableIterator<? extends T> iterator) {
		return new StandardCursor<>(iterator);
	}

	static <T> Cursor<T> create(Stream<T> stream) {
		return create(stream.iterator()).onClose(() -> stream.close());
	}
}
