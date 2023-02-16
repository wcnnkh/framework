package io.basc.framework.util;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@FunctionalInterface
public interface ResultSet<E> extends Iterable<E>, Streamy<E> {
	@Override
	Cursor<E> iterator();

	default Stream<E> stream() {
		return iterator().stream();
	}

	@Override
	default <U> ResultSet<U> flatConvert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
		return of(() -> iterator().flatConvert(converter));
	}

	@Override
	default <U> ResultSet<U> convert(Function<? super E, ? extends U> converter) {
		return of(() -> iterator().convert(converter));
	}

	@Override
	default <U> ResultSet<U> map(Function<? super E, ? extends U> mapper) {
		return of(() -> iterator().map(mapper));
	}

	@Override
	default ResultSet<E> filter(Predicate<? super E> predicate) {
		return of(() -> iterator().filter(predicate));
	}

	@Override
	default ResultSet<E> limit(Predicate<? super E> start, Predicate<? super E> end) {
		return of(() -> iterator().limit(start, end));
	}

	@Override
	default ResultSet<E> limit(BigInteger start, BigInteger count) {
		return of(() -> iterator().limit(start, count));
	}

	@Override
	default ResultSet<E> limit(long start) {
		return limit(BigInteger.valueOf(start), null);
	}

	@Override
	default ResultSet<E> limit(long start, long count) {
		return limit(BigInteger.valueOf(start), BigInteger.valueOf(count));
	}

	static <T> ResultSet<T> empty() {
		return new EmptyResultSet<>();
	}

	static <T> ResultSet<T> of(Supplier<? extends Cursor<T>> cursorSupplier) {
		return new StandardResultSet<>(cursorSupplier);
	}

	static <T> ResultSet<T> of(List<? extends T> list) {
		return new StandardResultSet<>(list);
	}
}
