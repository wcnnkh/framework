package run.soeasy.framework.core.collection;

import java.util.Comparator;
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
import java.util.stream.Stream;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.core.function.ThrowingFunction;

public interface StreamableWrapper<E, W extends Streamable<E>> extends Streamable<E>, Wrapper<W> {

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
				ThrowingFunction<? super Stream<E>, ? extends T, ? extends X> processor) throws X {
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
		default <X extends Throwable> void transfer(ThrowingConsumer<? super Stream<E>, ? extends X> processor)
				throws X {
			getSource().transfer(processor);
		}

		@Override
		default int hashCode(ToIntFunction<? super E> hash) {
			return getSource().hashCode(hash);
		}
	}
