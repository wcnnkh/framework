package io.basc.framework.util;

import java.util.Comparator;
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
import java.util.stream.Collector;
import java.util.stream.Stream;

public class StreamableWrapper<E, W extends Streamable<E>> extends Wrapper<W> implements Streamable<E> {

	public StreamableWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public boolean allMatch(Predicate<? super E> predicate) {
		return wrappedTarget.allMatch(predicate);
	}

	@Override
	public boolean anyMatch(Predicate<? super E> predicate) {
		return wrappedTarget.anyMatch(predicate);
	}

	@Override
	public <T> boolean anyMatch(Streamable<T> target, BiPredicate<? super E, ? super T> predicate) {
		return wrappedTarget.anyMatch(target, predicate);
	}

	@Override
	public <R, A> R collect(Collector<? super E, A, R> collector) {
		return wrappedTarget.collect(collector);
	}

	@Override
	public boolean contains(E element) {
		return wrappedTarget.contains(element);
	}

	@Override
	public long count() {
		return wrappedTarget.count();
	}

	@Override
	public <T, X extends Throwable> T export(Processor<? super Stream<E>, ? extends T, ? extends X> processor)
			throws X {
		return wrappedTarget.export(processor);
	}

	@Override
	public Optional<E> findAny() {
		return wrappedTarget.findAny();
	}

	@Override
	public Optional<E> findFirst() {
		return wrappedTarget.findFirst();
	}

	@Override
	public E first() {
		return wrappedTarget.first();
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		wrappedTarget.forEach(action);
	}

	@Override
	public void forEachOrdered(Consumer<? super E> action) {
		wrappedTarget.forEachOrdered(action);
	}

	@Override
	public boolean isEmpty() {
		return wrappedTarget.isEmpty();
	}

	@Override
	public E last() {
		return wrappedTarget.last();
	}

	@Override
	public Optional<E> max(Comparator<? super E> comparator) {
		return wrappedTarget.max(comparator);
	}

	@Override
	public Optional<E> min(Comparator<? super E> comparator) {
		return wrappedTarget.min(comparator);
	}

	@Override
	public boolean noneMatch(Predicate<? super E> predicate) {
		return wrappedTarget.noneMatch(predicate);
	}

	@Override
	public Optional<E> reduce(BinaryOperator<E> accumulator) {
		return wrappedTarget.reduce(accumulator);
	}

	@Override
	public E reduce(E identity, BinaryOperator<E> accumulator) {
		return wrappedTarget.reduce(identity, accumulator);
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super E, U> accumulator, BinaryOperator<U> combiner) {
		return wrappedTarget.reduce(identity, accumulator, combiner);
	}

	@Override
	public Stream<E> stream() {
		return wrappedTarget.stream();
	}

	@Override
	public boolean test(Predicate<? super Stream<E>> predicate) {
		return wrappedTarget.test(predicate);
	}

	@Override
	public Object[] toArray() {
		return wrappedTarget.toArray();
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		return wrappedTarget.toArray(generator);
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return wrappedTarget.toArray(array);
	}

	@Override
	public List<E> toList() {
		return wrappedTarget.toList();
	}

	@Override
	public <K> Map<K, E> toMap(Function<? super E, ? extends K> keyMapper) {
		return wrappedTarget.toMap(keyMapper);
	}

	@Override
	public <K, V> Map<K, V> toMap(Function<? super E, ? extends K> keyMapper,
			Function<? super E, ? extends V> valueMapper) {
		return wrappedTarget.toMap(keyMapper, valueMapper);
	}

	@Override
	public <K, V, M extends Map<K, V>> M toMap(Function<? super E, ? extends K> keyMapper,
			Function<? super E, ? extends V> valueMapper, Supplier<? extends M> mapSupplier) {
		return wrappedTarget.toMap(keyMapper, valueMapper, mapSupplier);
	}

	@Override
	public Set<E> toSet() {
		return wrappedTarget.toSet();
	}

	@Override
	public <X extends Throwable> void transfer(ConsumeProcessor<? super Stream<E>, ? extends X> processor) throws X {
		wrappedTarget.transfer(processor);
	}
}
