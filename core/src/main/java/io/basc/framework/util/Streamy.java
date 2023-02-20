package io.basc.framework.util;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;

@FunctionalInterface
public interface Streamy<E> extends Optional<E> {
	Stream<E> stream();

	default Streamy<E> limit(long start) {
		return limit(BigInteger.valueOf(start), null);
	}

	default Streamy<E> limit(long start, long count) {
		return limit(BigInteger.valueOf(start), BigInteger.valueOf(count));
	}

	default Streamy<E> limit(BigInteger start, @Nullable BigInteger count) {
		Stream<E> stream = stream();
		try {
			return new FilterableCursor<>(Cursor.of(stream.iterator()), start, count, () -> stream.close());
		} catch (Throwable e) {
			stream.close();
			throw e;
		}
	}

	default Streamy<E> limit(@Nullable Predicate<? super E> start, @Nullable Predicate<? super E> end) {
		Stream<E> stream = stream();
		try {
			return new FilterableCursor<>(Cursor.of(stream.iterator()), start, end, () -> stream.close());
		} catch (Throwable e) {
			stream.close();
			throw e;
		}
	}

	default <R, A> R collect(Collector<? super E, A, R> collector) {
		Stream<E> stream = stream();
		try {
			return stream.collect(collector);
		} finally {
			stream.close();
		}
	}

	default java.util.Optional<E> findFirst() {
		Stream<E> stream = stream();
		try {
			return stream.findFirst();
		} finally {
			stream.close();
		}
	}

	default java.util.Optional<E> findAny() {
		Stream<E> stream = stream();
		try {
			return stream.findAny();
		} finally {
			stream.close();
		}
	}

	@Override
	default E get() {
		return findFirst().get();
	}

	@Override
	default E orElse(E other) {
		return findFirst().orElse(other);
	}

	@Override
	default <X extends Throwable> E orElseGet(Source<? extends E, ? extends X> other) throws X {
		java.util.Optional<E> optional = findFirst();
		return optional.isPresent() ? optional.get() : other.get();
	}

	@Override
	default boolean isPresent() {
		return findFirst().isPresent();
	}

	default E first() {
		Stream<E> stream = stream();
		try {
			Iterator<E> iterator = stream.iterator();
			return iterator.hasNext() ? iterator.next() : null;
		} finally {
			stream.close();
		}
	}

	default List<E> toList() {
		return collect(Collectors.toList());
	}

	default Set<E> toSet() {
		return collect(Collectors.toCollection(() -> new LinkedHashSet<>()));
	}

	default <K> Map<K, E> toMap(Function<? super E, ? extends K> keyMapper) {
		return toMap(keyMapper, Function.identity());
	}

	default <K, V> Map<K, V> toMap(Function<? super E, ? extends K> keyMapper,
			Function<? super E, ? extends V> valueMapper) {
		return toMap(keyMapper, valueMapper, () -> new LinkedHashMap<>());
	}

	default <K, V, M extends Map<K, V>> M toMap(Function<? super E, ? extends K> keyMapper,
			Function<? super E, ? extends V> valueMapper, Supplier<? extends M> mapSupplier) {
		return collect(Collectors.toMap(keyMapper, valueMapper, (u, v) -> {
			throw new IllegalStateException(String.format("Duplicate key %s", u));
		}, mapSupplier));
	}

	default E last() {
		Stream<E> stream = stream();
		try {
			Iterator<E> iterator = stream.iterator();
			while (iterator.hasNext()) {
				E e = iterator.next();
				if (!iterator.hasNext()) {
					return e;
				}
			}
			return null;
		} finally {
			stream.close();
		}
	}

	default <U> Streamy<U> flatConvert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
		return () -> converter.apply(stream());
	}

	default <U> Streamy<U> convert(Function<? super E, ? extends U> converter) {
		return () -> stream().map(converter);
	}

	default <U> Streamy<U> map(Function<? super E, ? extends U> mapper) {
		return () -> stream().map(mapper);
	}

	default Streamy<E> filter(Predicate<? super E> predicate) {
		Stream<E> stream = stream();
		try {
			return new FilterableCursor<>(Cursor.of(stream.iterator()), predicate, () -> stream.close());
		} catch (Throwable e) {
			stream.close();
			throw e;
		}
	}

	default <T, X extends Throwable> T export(Processor<? super Stream<E>, ? extends T, ? extends X> processor)
			throws X {
		Stream<E> stream = stream();
		try {
			return processor.process(stream);
		} finally {
			stream.close();
		}
	}

	default <X extends Throwable> void transfer(ConsumeProcessor<? super Stream<E>, ? extends X> processor) throws X {
		Stream<E> stream = stream();
		try {
			processor.process(stream);
		} finally {
			stream.close();
		}
	}
}
