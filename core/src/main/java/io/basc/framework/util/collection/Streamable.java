package io.basc.framework.util.collection;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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

import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.function.Wrapper;

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

	public static interface StreamableWrapper<E, W extends Streamable<E>> extends Streamable<E>, Wrapper<W> {

		@Override
		default boolean allMatch(Predicate<? super E> predicate) {
			return getSource().allMatch(predicate);
		}

		@Override
		default boolean anyMatch(Predicate<? super E> predicate) {
			return getSource().anyMatch(predicate);
		}

		@Override
		default <T> boolean anyMatch(Streamable<T> target, BiPredicate<? super E, ? super T> predicate) {
			return getSource().anyMatch(target, predicate);
		}

		@Override
		default <R, A> R collect(Collector<? super E, A, R> collector) {
			return getSource().collect(collector);
		}

		@Override
		default boolean contains(Object element) {
			return getSource().contains(element);
		}

		@Override
		default long count() {
			return getSource().count();
		}

		@Override
		default <T> boolean equals(Streamable<? extends T> streamable, BiPredicate<? super E, ? super T> predicate) {
			return getSource().equals(streamable, predicate);
		}

		@Override
		default <T, X extends Throwable> T export(
				io.basc.framework.util.function.Function<? super Stream<E>, ? extends T, ? extends X> processor) throws X {
			return getSource().export(processor);
		}

		@Override
		default Optional<E> findAny() {
			return getSource().findAny();
		}

		@Override
		default Optional<E> findFirst() {
			return getSource().findFirst();
		}

		@Override
		default E first() {
			return getSource().first();
		}

		@Override
		default void forEach(Consumer<? super E> action) {
			getSource().forEach(action);
		}

		@Override
		default void forEachOrdered(Consumer<? super E> action) {
			getSource().forEachOrdered(action);
		}

		@Override
		default E getUnique() throws NoSuchElementException, NoUniqueElementException {
			return getSource().getUnique();
		}

		@Override
		default boolean isEmpty() {
			return getSource().isEmpty();
		}

		@Override
		default boolean isUnique() {
			return getSource().isUnique();
		}

		@Override
		default E last() {
			return getSource().last();
		}

		@Override
		default Optional<E> max(Comparator<? super E> comparator) {
			return getSource().max(comparator);
		}

		@Override
		default Optional<E> min(Comparator<? super E> comparator) {
			return getSource().min(comparator);
		}

		@Override
		default boolean noneMatch(Predicate<? super E> predicate) {
			return getSource().noneMatch(predicate);
		}

		@Override
		default Optional<E> reduce(BinaryOperator<E> accumulator) {
			return getSource().reduce(accumulator);
		}

		@Override
		default E reduce(E identity, BinaryOperator<E> accumulator) {
			return getSource().reduce(identity, accumulator);
		}

		@Override
		default <U> U reduce(U identity, BiFunction<U, ? super E, U> accumulator, BinaryOperator<U> combiner) {
			return getSource().reduce(identity, accumulator, combiner);
		}

		@Override
		default Stream<E> stream() {
			return getSource().stream();
		}

		@Override
		default boolean test(Predicate<? super Stream<E>> predicate) {
			return getSource().test(predicate);
		}

		@Override
		default Object[] toArray() {
			return getSource().toArray();
		}

		@Override
		default <A> A[] toArray(IntFunction<A[]> generator) {
			return getSource().toArray(generator);
		}

		@Override
		default <T> T[] toArray(T[] array) {
			return getSource().toArray(array);
		}

		@Override
		default List<E> toList() {
			return getSource().toList();
		}

		@Override
		default <K> Map<K, E> toMap(Function<? super E, ? extends K> keyMapper) {
			return getSource().toMap(keyMapper);
		}

		@Override
		default <K, V> Map<K, V> toMap(Function<? super E, ? extends K> keyMapper,
				Function<? super E, ? extends V> valueMapper) {
			return getSource().toMap(keyMapper, valueMapper);
		}

		@Override
		default <K, V, M extends Map<K, V>> M toMap(Function<? super E, ? extends K> keyMapper,
				Function<? super E, ? extends V> valueMapper, Supplier<? extends M> mapSupplier) {
			return getSource().toMap(keyMapper, valueMapper, mapSupplier);
		}

		@Override
		default Set<E> toSet() {
			return getSource().toSet();
		}

		@Override
		default <X extends Throwable> void transfer(
				io.basc.framework.util.function.Consumer<? super Stream<E>, ? extends X> processor) throws X {
			getSource().transfer(processor);
		}

		@Override
		default int hashCode(ToIntFunction<? super E> hash) {
			return getSource().hashCode(hash);
		}
	}

	public static class EmptyStreamable<E> implements Streamable<E>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public Stream<E> stream() {
			return Stream.empty();
		}
	}

	public static final EmptyStreamable<Object> EMPTY_STREAMABLE = new EmptyStreamable<>();

	@SuppressWarnings("unchecked")
	public static <T> Streamable<T> empty() {
		return (Streamable<T>) EMPTY_STREAMABLE;
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
	 * 调用{@link #export(Function)}
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

	default <T, X extends Throwable> T export(
			io.basc.framework.util.function.Function<? super Stream<E>, ? extends T, ? extends X> processor) throws X {
		Stream<E> stream = stream();
		try {
			return processor.apply(stream);
		} finally {
			stream.close();
		}
	}

	/**
	 * 调用{@link #export(Function)}
	 * 
	 * @return
	 */
	default Optional<E> findAny() {
		return export((stream) -> stream.findAny());
	}

	/**
	 * 调用{@link #export(Function)}
	 * 
	 * @return
	 */
	default Optional<E> findFirst() {
		return export((stream) -> stream.findFirst());
	}

	/**
	 * 调用{@link #export(Function)}
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

	/**
	 * 获取唯一的元素， 默认使用迭代器实现
	 * 
	 * @see #isUnique()
	 * @return
	 * @throws NoSuchElementException   没有元素
	 * @throws NoUniqueElementException 存在多个元素
	 */
	default E getUnique() throws NoSuchElementException, NoUniqueElementException {
		return export((stream) -> {
			Iterator<E> iterator = stream.iterator();
			if (!iterator.hasNext()) {
				throw new NoSuchElementException();
			}

			// 向后迭代一次
			E element = iterator.next();
			if (iterator.hasNext()) {
				// 如果还有说明不是只有一个
				throw new NoUniqueElementException();
			}
			return element;
		});
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

	default boolean isEmpty() {
		return !findAny().isPresent();
	}

	/**
	 * 是否只有一个元素
	 * 
	 * @see #getUnique()
	 * @return
	 */
	default boolean isUnique() {
		return test((stream) -> {
			Iterator<E> iterator = stream.iterator();
			if (iterator.hasNext()) {
				return false;
			}

			// 向后迭代一次
			iterator.next();
			if (iterator.hasNext()) {
				// 如果还有说明不是只有一个
				return false;
			}
			return true;
		});
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
	 * @see #export(Function)
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

	default <X extends Throwable> void transfer(
			io.basc.framework.util.function.Consumer<? super Stream<E>, ? extends X> processor) throws X {
		Stream<E> stream = stream();
		try {
			processor.accept(stream);
		} finally {
			stream.close();
		}
	}
}
