package scw.util.stream;

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

import scw.lang.Nullable;

/**
 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法
 * @author shuchaowen
 *
 * @param <T>
 * @param <S>
 */
public abstract class AbstractAutoCloseStream<T, S extends Stream<T>>
		extends AbstractStreamWrapper<T, Stream<T>, S> implements Stream<T> {

	public AbstractAutoCloseStream(Stream<T> stream) {
		super(stream);
	}
	
	public List<T> shared() {
		return collect(Collectors.toList());
	}

	@Nullable
	public T first() {
		return findFirst().orElse(null);
	}
	
	@Override
	public <R> AutoCloseStream<R> map(Function<? super T, ? extends R> mapper) {
		Stream<R> stream = this.wrappedTarget.map(mapper);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<R>) stream;
		}
		return new AutoCloseStream<>(stream);
	}

	@Override
	public AutoCloseIntStream mapToInt(ToIntFunction<? super T> mapper) {
		IntStream stream = this.wrappedTarget.mapToInt(mapper);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public AutoCloseLongStream mapToLong(ToLongFunction<? super T> mapper) {
		LongStream stream = this.wrappedTarget.mapToLong(mapper);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStream(stream);
	}

	@Override
	public AutoCloseDoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
		DoubleStream stream = this.wrappedTarget.mapToDouble(mapper);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStream(stream);
	}

	@Override
	public <R> AutoCloseStream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		Stream<R> stream = this.wrappedTarget.flatMap(mapper);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<R>) stream;
		}
		return new AutoCloseStream<>(stream);
	}

	@Override
	public AutoCloseIntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		IntStream stream = this.wrappedTarget.flatMapToInt(mapper);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public AutoCloseLongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		LongStream stream = this.wrappedTarget.flatMapToLong(mapper);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStream(stream);
	}

	@Override
	public AutoCloseDoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		DoubleStream stream = this.wrappedTarget.flatMapToDouble(mapper);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStream(stream);
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		try {
			this.wrappedTarget.forEach(action);
		} finally {
			close();
		}
	}

	@Override
	public void forEachOrdered(Consumer<? super T> action) {
		try {
			this.wrappedTarget.forEachOrdered(action);
		} finally {
			close();
		}
	}

	@Override
	public Object[] toArray() {
		try {
			return this.wrappedTarget.toArray();
		} finally {
			close();
		}
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		try {
			return this.wrappedTarget.toArray(generator);
		} finally {
			close();
		}
	}

	@Override
	public T reduce(T identity, BinaryOperator<T> accumulator) {
		try {
			return this.wrappedTarget.reduce(identity, accumulator);
		} finally {
			close();
		}
	}

	@Override
	public Optional<T> reduce(BinaryOperator<T> accumulator) {
		try {
			return this.wrappedTarget.reduce(accumulator);
		} finally {
			close();
		}
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		try {
			return this.wrappedTarget.reduce(identity, accumulator, combiner);
		} finally {
			close();
		}
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		try {
			return this.wrappedTarget.collect(supplier, accumulator, combiner);
		} finally {
			close();
		}
	}

	@Override
	public <R, A> R collect(Collector<? super T, A, R> collector) {
		try {
			return this.wrappedTarget.collect(collector);
		} finally {
			close();
		}
	}

	@Override
	public Optional<T> min(Comparator<? super T> comparator) {
		try {
			return this.wrappedTarget.min(comparator);
		} finally {
			close();
		}
	}

	@Override
	public Optional<T> max(Comparator<? super T> comparator) {
		try {
			return this.wrappedTarget.max(comparator);
		} finally {
			close();
		}
	}

	@Override
	public long count() {
		try {
			return this.wrappedTarget.count();
		} finally {
			close();
		}
	}

	@Override
	public boolean anyMatch(Predicate<? super T> predicate) {
		try {
			return this.wrappedTarget.anyMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean allMatch(Predicate<? super T> predicate) {
		try {
			return this.wrappedTarget.allMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean noneMatch(Predicate<? super T> predicate) {
		try {
			return this.wrappedTarget.noneMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public Optional<T> findFirst() {
		try {
			return this.wrappedTarget.findFirst();
		} finally {
			close();
		}
	}

	@Override
	public Optional<T> findAny() {
		try {
			return this.wrappedTarget.findAny();
		} finally {
			close();
		}
	}

}
