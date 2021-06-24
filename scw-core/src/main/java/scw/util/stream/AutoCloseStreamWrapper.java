package scw.util.stream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
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
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import scw.core.utils.ObjectUtils;

public class AutoCloseStreamWrapper<T> implements AutoCloseStream<T> {
	private final Stream<T> stream;

	public AutoCloseStreamWrapper(Stream<T> stream) {
		this.stream = stream;
	}

	@Override
	public Iterator<T> iterator() {
		return stream.iterator();
	}

	@Override
	public Spliterator<T> spliterator() {
		return stream.spliterator();
	}

	@Override
	public boolean isParallel() {
		return stream.isParallel();
	}

	@Override
	public AutoCloseStream<T> sequential() {
		Stream<T> stream = this.stream.sequential();
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>)stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseStream<T> parallel() {
		Stream<T> stream = this.stream.parallel();
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>)stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseStream<T> unordered() {
		Stream<T> stream = this.stream.unordered();
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>)stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseStream<T> onClose(Runnable closeHandler) {
		Stream<T> stream = this.stream.onClose(closeHandler);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>)stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public void close() {
		stream.close();
	}

	@Override
	public AutoCloseStream<T> filter(Predicate<? super T> predicate) {
		Stream<T> stream = this.stream.filter(predicate);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public <R> AutoCloseStream<R> map(Function<? super T, ? extends R> mapper) {
		Stream<R> stream = this.stream.map(mapper);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<R>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseIntStream mapToInt(ToIntFunction<? super T> mapper) {
		IntStream stream = this.stream.mapToInt(mapper);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream mapToLong(ToLongFunction<? super T> mapper) {
		LongStream stream = this.stream.mapToLong(mapper);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
		DoubleStream stream = this.stream.mapToDouble(mapper);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public <R> AutoCloseStream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		Stream<R> stream = this.stream.flatMap(mapper);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<R>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseIntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		IntStream stream = this.stream.flatMapToInt(mapper);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		LongStream stream = this.stream.flatMapToLong(mapper);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		DoubleStream stream = this.stream.flatMapToDouble(mapper);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseStream<T> distinct() {
		Stream<T> stream = this.stream.distinct();
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseStream<T> sorted() {
		Stream<T> stream = this.stream.sorted();
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseStream<T> sorted(Comparator<? super T> comparator) {
		Stream<T> stream = this.stream.sorted();
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseStream<T> peek(Consumer<? super T> action) {
		Stream<T> stream = this.stream.peek(action);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseStream<T> limit(long maxSize) {
		Stream<T> stream = this.stream.limit(maxSize);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseStream<T> skip(long n) {
		Stream<T> stream = this.stream.skip(n);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		try {
			this.stream.forEach(action);
		} finally {
			close();
		}
	}

	@Override
	public void forEachOrdered(Consumer<? super T> action) {
		try {
			this.stream.forEachOrdered(action);
		} finally {
			close();
		}
	}

	@Override
	public Object[] toArray() {
		try {
			return this.stream.toArray();
		} finally {
			close();
		}
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		try {
			return this.stream.toArray(generator);
		} finally {
			close();
		}
	}

	@Override
	public T reduce(T identity, BinaryOperator<T> accumulator) {
		try {
			return this.stream.reduce(identity, accumulator);
		} finally {
			close();
		}
	}

	@Override
	public Optional<T> reduce(BinaryOperator<T> accumulator) {
		try {
			return this.stream.reduce(accumulator);
		} finally {
			close();
		}
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		try {
			return this.stream.reduce(identity, accumulator, combiner);
		} finally {
			close();
		}
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		try {
			return this.stream.collect(supplier, accumulator, combiner);
		} finally {
			close();
		}
	}

	@Override
	public <R, A> R collect(Collector<? super T, A, R> collector) {
		try {
			return this.stream.collect(collector);
		} finally {
			close();
		}
	}

	@Override
	public Optional<T> min(Comparator<? super T> comparator) {
		try {
			return this.stream.min(comparator);
		} finally {
			close();
		}
	}

	@Override
	public Optional<T> max(Comparator<? super T> comparator) {
		try {
			return this.stream.max(comparator);
		} finally {
			close();
		}
	}

	@Override
	public long count() {
		try {
			return this.stream.count();
		} finally {
			close();
		}
	}

	@Override
	public boolean anyMatch(Predicate<? super T> predicate) {
		try {
			return this.stream.anyMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean allMatch(Predicate<? super T> predicate) {
		try {
			return this.stream.allMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean noneMatch(Predicate<? super T> predicate) {
		try {
			return this.stream.noneMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public Optional<T> findFirst() {
		try {
			return this.stream.findFirst();
		} finally {
			close();
		}
	}

	@Override
	public Optional<T> findAny() {
		try {
			return this.stream.findAny();
		} finally {
			close();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof AutoCloseStreamWrapper) {
			return ObjectUtils.nullSafeEquals(((AutoCloseStreamWrapper<?>) obj).stream, this.stream);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.stream.hashCode();
	}

	@Override
	public String toString() {
		return this.stream.toString();
	}
}
