package io.basc.framework.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 就像{@link Iterable}可以返回{@link Iterator}一样，{@link Streamable}可以返回{@link Stream}
 * 此类其他方法调用{@link Streamable#stream()}后会自动关闭
 * 
 * <p>
 * 此类的方法除{@link #stream()}外都是end point
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
		return test((stream) -> stream.allMatch(predicate));
	}

	default boolean anyMatch(Predicate<? super E> predicate) {
		return test((stream) -> stream.anyMatch(predicate));
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

	default boolean contains(Object element) {
		return anyMatch((e) -> e == element || ObjectUtils.equals(e, element));
	}

	default long count() {
		Stream<E> stream = stream();
		try {
			return stream.count();
		} finally {
			stream.close();
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

	/**
	 * 调用{@link #export(Processor)}
	 * 
	 * @return
	 */
	default Optional<E> findAny() {
		return export((stream) -> stream.findAny());
	}

	/**
	 * 调用{@link #export(Processor)}
	 * 
	 * @return
	 */
	default Optional<E> findFirst() {
		return export((stream) -> stream.findFirst());
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

	default void forEach(Consumer<? super E> action) {
		Assert.requiredArgument(action != null, "action");
		transfer((stream) -> stream.forEach(action));
	}

	default void forEachOrdered(Consumer<? super E> action) {
		Assert.requiredArgument(action != null, "action");
		transfer((stream) -> stream.forEachOrdered(action));
	}

	default boolean isEmpty() {
		return !findAny().isPresent();
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

	default Optional<E> max(Comparator<? super E> comparator) {
		return export((stream) -> stream.max(comparator));
	}

	default Optional<E> min(Comparator<? super E> comparator) {
		return export((stream) -> stream.min(comparator));
	}

	default boolean noneMatch(Predicate<? super E> predicate) {
		return test((stream) -> stream.noneMatch(predicate));
	}

	default Optional<E> reduce(BinaryOperator<E> accumulator) {
		return export((stream) -> stream.reduce(accumulator));
	}

	default E reduce(E identity, BinaryOperator<E> accumulator) {
		return export((stream) -> stream.reduce(identity, accumulator));
	}

	default <U> U reduce(U identity, BiFunction<U, ? super E, U> accumulator, BinaryOperator<U> combiner) {
		return export((stream) -> stream.reduce(identity, accumulator, combiner));
	}

	/**
	 * 一般情况下记得关闭
	 * 
	 * @return
	 */
	Stream<E> stream();

	default boolean test(Predicate<? super Stream<E>> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		Stream<E> stream = stream();
		try {
			return predicate.test(stream);
		} finally {
			stream.close();
		}
	}

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

	default int hashCode(ToIntFunction<? super E> hash) {
		Assert.requiredArgument(hash != null, "hash");
		Stream<E> stream = stream();
		try {
			Iterator<E> iterator = stream.iterator();
			if (!iterator.hasNext()) {
				return 0;
			}

			int result = 1;
			while (iterator.hasNext()) {
				E element = iterator.next();
				result = 31 * result + (element == null ? 0 : hash.applyAsInt(element));
			}
			return result;
		} finally {
			stream.close();
		}
	}

	default <T> boolean equals(Streamable<? extends T> streamable, BiPredicate<? super E, ? super T> predicate) {
		Assert.requiredArgument(streamable != null, "streamable");
		Assert.requiredArgument(predicate != null, "predicate");
		Stream<E> stream = stream();
		try {
			Stream<? extends T> targetStream = streamable.stream();
			try {
				Iterator<E> sourceIterator = stream.iterator();
				Iterator<? extends T> targetIterator = targetStream.iterator();
				while (sourceIterator.hasNext() && targetIterator.hasNext()) {
					E source = sourceIterator.next();
					T target = targetIterator.next();
					if (source == target) {
						continue;
					}

					// 如果都为空已经在上一步拦截了
					if (source == null || target == null) {
						return false;
					}

					if (predicate.test(source, target)) {
						continue;
					}
					return false;
				}

				// 都没有了才算相等
				return !sourceIterator.hasNext() && !targetIterator.hasNext();
			} finally {
				targetStream.close();
			}
		} finally {
			stream.close();
		}
	}
}
