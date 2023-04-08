package io.basc.framework.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
	public <R, A> R collect(Collector<? super E, A, R> collector) {
		return wrappedTarget.collect(collector);
	}

	@Override
	public <U> Streamable<U> convert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
		return wrappedTarget.convert(converter);
	}

	@Override
	public <T, X extends Throwable> T export(Processor<? super Stream<E>, ? extends T, ? extends X> processor)
			throws X {
		return wrappedTarget.export(processor);
	}

	@Override
	public Streamable<E> filter(Predicate<? super E> predicate) {
		return wrappedTarget.filter(predicate);
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
	public <U> Streamable<U> flatMap(Function<? super E, ? extends Streamable<U>> mapper) {
		return wrappedTarget.flatMap(mapper);
	}

	@Override
	public E last() {
		return wrappedTarget.last();
	}

	@Override
	public <U> Streamable<U> map(Function<? super E, ? extends U> mapper) {
		return wrappedTarget.map(mapper);
	}

	@Override
	public Stream<E> stream() {
		return wrappedTarget.stream();
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		wrappedTarget.forEach(action);
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
