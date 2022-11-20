package io.basc.framework.util;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FunctionalInterface
public interface Streamy<E> extends io.basc.framework.util.Optional<E> {
	Stream<E> stream();

	default <R, A> R collect(Collector<? super E, A, R> collector) {
		Stream<E> stream = stream();
		try {
			return stream.collect(collector);
		} finally {
			stream.close();
		}
	}

	default E first() {
		return findFirst().orElse(null);
	}

	default Optional<E> findFirst() {
		Stream<E> stream = stream();
		try {
			return stream.findFirst();
		} finally {
			stream.close();
		}
	}

	default Optional<E> findAny() {
		Stream<E> stream = stream();
		try {
			return stream.findAny();
		} finally {
			stream.close();
		}
	}

	/**
	 * @see #findAny()
	 */
	@Override
	default E get() {
		return findAny().get();
	}

	/**
	 * @see #findAny()
	 */
	default boolean isPresent() {
		return findAny().isPresent();
	}

	default List<E> toList() {
		return collect(Collectors.toList());
	}

	default Set<E> toSet() {
		return collect(Collectors.toCollection(() -> new LinkedHashSet<>()));
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

	@Override
	default <U> Streamy<U> map(Function<? super E, ? extends U> mapper) {
		return () -> stream().map(mapper);
	}

	@Override
	default Streamy<E> filter(Predicate<? super E> predicate) {
		return () -> stream().filter(predicate);
	}

	default <X extends Throwable> void export(ConsumeProcessor<? super Stream<E>, ? extends X> processor) throws X {
		Stream<E> stream = stream();
		try {
			processor.process(stream);
		} finally {
			stream.close();
		}
	}
}
