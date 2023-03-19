package io.basc.framework.util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
		return Cursor.of(mapper.apply(stream()));
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
		return of(Collections.emptyIterator());
	}

	@SuppressWarnings("unchecked")
	static <T> Cursor<T> of(Iterator<? extends T> iterator) {
		if (iterator == null) {
			return empty();
		}

		if (iterator instanceof Cursor) {
			return (Cursor<T>) iterator;
		}

		if (iterator instanceof CloseableIterator) {
			return new StandardCursor<>((CloseableIterator<? extends T>) iterator);
		}

		return new StandardCursor<>(iterator);
	}

	static <T> Cursor<T> of(Stream<? extends T> stream) {
		if (stream == null) {
			return empty();
		}

		Cursor<T> cursor = of(stream.iterator());
		return cursor.onClose(() -> stream.close());
	}

	static <T> Cursor<T> of(Iterable<? extends T> iterable) {
		if (iterable == null) {
			return empty();
		}

		Iterator<? extends T> iterator;
		if (iterable instanceof List) {
			iterator = ((List<? extends T>) iterable).listIterator();
		} else {
			iterator = iterable.iterator();
		}
		return new StandardCursor<>(iterator);
	}

	@SafeVarargs
	static <T> Cursor<T> of(T... values) {
		if (values == null) {
			return empty();
		}

		return of(Arrays.asList(values));
	}

}
