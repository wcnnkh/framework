package io.basc.framework.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 就像{@link Iterable}可以返回{@link Iterator}一样，{@link Streamable}可以返回{@link Stream}
 * 此类其他方法调用{@link Streamable#stream()}后会自动关闭
 * 
 * @author wcnnkh
 * @see Iterable#iterator()
 * @see Streamable#stream()
 *
 * @param <T>
 */
@FunctionalInterface
public interface Streamable<E> {

	@SuppressWarnings("unchecked")
	public static <T> Streamable<T> empty() {
		return (Streamable<T>) EmptyStreamable.INSTANCE;
	}

	default boolean allMatch(Predicate<? super E> predicate) {
		Stream<E> stream = stream();
		try {
			return stream.allMatch(predicate);
		} finally {
			stream.close();
		}
	}

	default boolean anyMatch(Predicate<? super E> predicate) {
		Stream<E> stream = stream();
		try {
			return stream.anyMatch(predicate);
		} finally {
			stream.close();
		}
	}

	default <T> boolean anyMatch(Streamable<T> target, BiPredicate<? super E, ? super T> predicate) {
		return anyMatch((s) -> target.anyMatch(((t) -> predicate.test(s, t))));
	}

	/**
	 * 调用{@link #export(Processor)}
	 * 
	 * @param <R>
	 * @param <A>
	 * @param collector
	 * @return
	 */
	default <R, A> R collect(Collector<? super E, A, R> collector) {
		return export((stream) -> stream.collect(collector));
	}

	default <U> Streamable<U> convert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
		return () -> converter.apply(stream());
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

	/**
	 * 默认调用{@link Streamable#convert(Function)}方法
	 * 
	 * @param predicate
	 * @return
	 */
	default Streamable<E> filter(Predicate<? super E> predicate) {
		return convert((stream) -> stream.filter(predicate));
	}

	/**
	 * 调用{@link #export(Processor)}
	 * 
	 * @return
	 */
	default Optional<E> findAny() {
		return export((stream) -> {
			java.util.Optional<E> optional = stream.findAny();
			return Optional.ofSupplier(() -> {
				return optional.isPresent() ? optional.get() : null;
			});
		});
	}

	/**
	 * 调用{@link #export(Processor)}
	 * 
	 * @return
	 */
	default Optional<E> findFirst() {
		return export((stream) -> {
			java.util.Optional<E> optional = stream.findFirst();
			return Optional.ofSupplier(() -> {
				return optional.isPresent() ? optional.get() : null;
			});
		});
	}

	/**
	 * 调用{@link #export(Processor)}
	 * 
	 * @return
	 */
	default E first() {
		return export((stream) -> {
			Iterator<E> iterator = stream.iterator();
			return iterator.hasNext() ? iterator.next() : null;
		});
	}

	/**
	 * 默认调用{@link Streamable#convert(Function)}方法
	 * 
	 * @see Stream#flatMap(Function)
	 * @param <U>
	 * @param mapper
	 * @return
	 */
	default <U> Streamable<U> flatMap(Function<? super E, ? extends Streamable<U>> mapper) {
		return convert((stream) -> {
			return stream.flatMap((e) -> {
				Streamable<U> streamy = mapper.apply(e);
				return streamy == null ? Stream.empty() : streamy.stream();
			});
		});
	}

	/**
	 * 默认使用{@link #stream()}的调用
	 * 
	 * @see Stream#forEachOrdered(Consumer)
	 */
	default void forEach(Consumer<? super E> action) {
		Assert.requiredArgument(action != null, "action");
		Stream<E> stream = stream();
		try {
			stream.forEachOrdered(action);
		} finally {
			stream.close();
		}
	}

	default E last() {
		return export((stream) -> {
			Iterator<E> iterator = stream.iterator();
			while (iterator.hasNext()) {
				E e = iterator.next();
				if (!iterator.hasNext()) {
					return e;
				}
			}
			return null;
		});
	}

	/**
	 * 默认调用{@link Streamable#convert(Function)}方法
	 * 
	 * @param <U>
	 * @param mapper
	 * @return
	 */
	default <U> Streamable<U> map(Function<? super E, ? extends U> mapper) {
		return convert((stream) -> stream.map(mapper));
	}

	/**
	 * 一般情况下记得关闭
	 * 
	 * @return
	 */
	Stream<E> stream();

	/**
	 * @see #toList()
	 * @see List#toArray()
	 * @return
	 */
	default Object[] toArray() {
		return toList().toArray();
	}

	/**
	 * @see #export(Processor)
	 * @param <A>
	 * @param generator
	 * @return
	 */
	default <A> A[] toArray(IntFunction<A[]> generator) {
		return export((stream) -> stream.toArray(generator));
	}

	/**
	 * @see #toList()
	 * @see List#toArray(Object[])
	 * @param <T>
	 * @param array
	 * @return
	 */
	default <T> T[] toArray(T[] array) {
		return toList().toArray(array);
	}

	/**
	 * @see #collect(Collector)
	 * @return
	 */
	default List<E> toList() {
		return collect(Collectors.toList());
	}

	/**
	 * @see #toMap(Function, Function)
	 * @param <K>
	 * @param keyMapper
	 * @return
	 */
	default <K> Map<K, E> toMap(Function<? super E, ? extends K> keyMapper) {
		return toMap(keyMapper, Function.identity());
	}

	/**
	 * @see #toMap(Function, Function, Supplier)
	 * @param <K>
	 * @param <V>
	 * @param keyMapper
	 * @param valueMapper
	 * @return
	 */
	default <K, V> Map<K, V> toMap(Function<? super E, ? extends K> keyMapper,
			Function<? super E, ? extends V> valueMapper) {
		return toMap(keyMapper, valueMapper, () -> new LinkedHashMap<>());
	}

	/**
	 * @see #collect(Collector)
	 * @param <K>
	 * @param <V>
	 * @param <M>
	 * @param keyMapper
	 * @param valueMapper
	 * @param mapSupplier
	 * @return
	 */
	default <K, V, M extends Map<K, V>> M toMap(Function<? super E, ? extends K> keyMapper,
			Function<? super E, ? extends V> valueMapper, Supplier<? extends M> mapSupplier) {
		return collect(Collectors.toMap(keyMapper, valueMapper, (u, v) -> {
			throw new IllegalStateException(String.format("Duplicate key %s", u));
		}, mapSupplier));
	}

	/**
	 * @see #collect(Collector)
	 * @return
	 */
	default Set<E> toSet() {
		return collect(Collectors.toCollection(() -> new LinkedHashSet<>()));
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
