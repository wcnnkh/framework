package io.basc.framework.util;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Cursor<E> extends CloseableIterator<E>, Streamy<E>, StreamOptional<E> {

	BigInteger getPosition();

	Cursor<E> onClose(RunnableProcessor<? extends RuntimeException> close) throws RuntimeException;

	default Stream<E> stream() {
		return XUtils.stream(this).onClose(() -> close());
	}

	@Override
	default <U> Cursor<U> convert(Function<? super E, ? extends U> converter) {
		Cursor<U> cursor = new StandardCursor<>(new ConvertibleIterator<E, U>(this, converter), getPosition());
		return cursor.onClose(() -> close());
	}

	default <T> Cursor<T> map(Function<? super E, ? extends T> mapper) {
		Cursor<T> cursor = new StandardCursor<>(new ConvertibleIterator<E, T>(this, mapper), getPosition());
		return cursor.onClose(() -> close());
	}

	default Cursor<E> filter(Predicate<? super E> predicate) {
		Cursor<E> cursor = new StandardCursor<>(new PredicateIterator<>(this, predicate), getPosition());
		return cursor.onClose(() -> close());
	}

	default Cursor<E> limit(long start, long limit) {
		return limit(BigInteger.valueOf(start), BigInteger.valueOf(limit));
	}

	/**
	 * 限制
	 * 
	 * @param start
	 * @param limit 小于0代表不限制
	 * @return
	 */
	default Cursor<E> limit(BigInteger start, BigInteger limit) {
		Cursor<E> cursor = new StandardCursor<>(new LimitIterator<>(this, start, start.add(limit)), start);
		return cursor.onClose(() -> close());
	}

	@Override
	default void forEachRemaining(Consumer<? super E> action) {
		try {
			CloseableIterator.super.forEachRemaining(action);
		} finally {
			close();
		}
	}

	@Override
	default E get() throws RuntimeException {
		return Streamy.super.get();
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
