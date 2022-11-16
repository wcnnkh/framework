package io.basc.framework.util;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface ResultSet<E> extends Iterable<E>, Streamy<E> {
	@Override
	Cursor<E> iterator();

	default Stream<E> stream() {
		return iterator().stream();
	}

	@Override
	default <U> Streamy<U> map(Function<? super E, ? extends U> mapper) {
		return of(() -> iterator().map(mapper));
	}

	@Override
	default Streamy<E> filter(Predicate<? super E> predicate) {
		return of(() -> iterator().filter(predicate));
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
