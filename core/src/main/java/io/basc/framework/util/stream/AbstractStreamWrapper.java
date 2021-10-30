package io.basc.framework.util.stream;

import io.basc.framework.lang.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class AbstractStreamWrapper<T, S extends Stream<T>> extends BaseStreamWrapper<T, Stream<T>>
		implements Stream<T> {

	public AbstractStreamWrapper(Stream<T> stream) {
		super(stream);
	}

	public List<T> shared() {
		return collect(Collectors.toList());
	}

	@Nullable
	public T first() {
		return findFirst().orElse(null);
	}

	protected abstract S wrap(Stream<T> stream);
	
	@Override
	public S sequential() {
		Stream<T> stream = this.wrappedTarget.sequential();
		return wrap(stream);
	}

	@Override
	public S parallel() {
		Stream<T> stream = this.wrappedTarget.parallel();
		return wrap(stream);
	}

	@Override
	public S unordered() {
		Stream<T> stream = this.wrappedTarget.unordered();
		return wrap(stream);
	}

	@Override
	public S onClose(Runnable closeHandler) {
		Stream<T> stream = this.wrappedTarget.onClose(closeHandler);
		return wrap(stream);
	}
	
	@Override
	public S filter(Predicate<? super T> predicate) {
		Stream<T> stream = this.wrappedTarget.filter(predicate);
		return wrap(stream);
	}

	@Override
	public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
		return this.wrappedTarget.map(mapper);
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super T> mapper) {
		return this.wrappedTarget.mapToInt(mapper);
	}

	@Override
	public LongStream mapToLong(ToLongFunction<? super T> mapper) {
		return this.wrappedTarget.mapToLong(mapper);
	}

	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
		return this.wrappedTarget.mapToDouble(mapper);
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		return this.wrappedTarget.flatMap(mapper);
	}

	@Override
	public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		return this.wrappedTarget.flatMapToInt(mapper);
	}

	@Override
	public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		return this.wrappedTarget.flatMapToLong(mapper);
	}

	@Override
	public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		return this.wrappedTarget.flatMapToDouble(mapper);
	}

	@Override
	public S distinct() {
		Stream<T> stream = this.wrappedTarget.distinct();
		return wrap(stream);
	}

	@Override
	public S sorted() {
		Stream<T> stream = this.wrappedTarget.sorted();
		return wrap(stream);
	}

	@Override
	public S sorted(Comparator<? super T> comparator) {
		Stream<T> stream = this.wrappedTarget.sorted();
		return wrap(stream);
	}

	@Override
	public S peek(Consumer<? super T> action) {
		Stream<T> stream = this.wrappedTarget.peek(action);
		return wrap(stream);
	}

	@Override
	public S limit(long maxSize) {
		Stream<T> stream = this.wrappedTarget.limit(maxSize);
		return wrap(stream);
	}

	@Override
	public S skip(long n) {
		Stream<T> stream = this.wrappedTarget.skip(n);
		return wrap(stream);
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		try {
			beforeExecution();
			this.wrappedTarget.forEach(action);
		} finally {
			afterExecution();
		}
	}

	@Override
	public void forEachOrdered(Consumer<? super T> action) {
		try {
			beforeExecution();
			this.wrappedTarget.forEachOrdered(action);
		} finally {
			afterExecution();
		}
	}

	@Override
	public Object[] toArray() {
		try {
			beforeExecution();
			return this.wrappedTarget.toArray();
		} finally {
			afterExecution();
		}
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		try {
			beforeExecution();
			return this.wrappedTarget.toArray(generator);
		} finally {
			afterExecution();
		}
	}

	@Override
	public T reduce(T identity, BinaryOperator<T> accumulator) {
		try {
			beforeExecution();
			return this.wrappedTarget.reduce(identity, accumulator);
		} finally {
			afterExecution();
		}
	}

	@Override
	public Optional<T> reduce(BinaryOperator<T> accumulator) {
		try {
			beforeExecution();
			return this.wrappedTarget.reduce(accumulator);
		} finally {
			afterExecution();
		}
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		try {
			beforeExecution();
			return this.wrappedTarget.reduce(identity, accumulator, combiner);
		} finally {
			afterExecution();
		}
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		try {
			beforeExecution();
			return this.wrappedTarget.collect(supplier, accumulator, combiner);
		} finally {
			afterExecution();
		}
	}

	@Override
	public <R, A> R collect(Collector<? super T, A, R> collector) {
		try {
			beforeExecution();
			return this.wrappedTarget.collect(collector);
		} finally {
			afterExecution();
		}
	}

	@Override
	public Optional<T> min(Comparator<? super T> comparator) {
		try {
			beforeExecution();
			return this.wrappedTarget.min(comparator);
		} finally {
			afterExecution();
		}
	}

	@Override
	public Optional<T> max(Comparator<? super T> comparator) {
		try {
			beforeExecution();
			return this.wrappedTarget.max(comparator);
		} finally {
			afterExecution();
		}
	}

	@Override
	public long count() {
		try {
			beforeExecution();
			return this.wrappedTarget.count();
		} finally {
			afterExecution();
		}
	}

	@Override
	public boolean anyMatch(Predicate<? super T> predicate) {
		try {
			beforeExecution();
			return this.wrappedTarget.anyMatch(predicate);
		} finally {
			afterExecution();
		}
	}

	@Override
	public boolean allMatch(Predicate<? super T> predicate) {
		try {
			beforeExecution();
			return this.wrappedTarget.allMatch(predicate);
		} finally {
			afterExecution();
		}
	}

	@Override
	public boolean noneMatch(Predicate<? super T> predicate) {
		try {
			beforeExecution();
			return this.wrappedTarget.noneMatch(predicate);
		} finally {
			afterExecution();
		}
	}

	@Override
	public Optional<T> findFirst() {
		try {
			beforeExecution();
			return this.wrappedTarget.findFirst();
		} finally {
			afterExecution();
		}
	}

	@Override
	public Optional<T> findAny() {
		try {
			beforeExecution();
			return this.wrappedTarget.findAny();
		} finally {
			afterExecution();
		}
	}
}