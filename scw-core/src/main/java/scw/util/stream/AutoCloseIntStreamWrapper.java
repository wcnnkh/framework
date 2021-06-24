package scw.util.stream;

import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import scw.core.utils.ObjectUtils;

public class AutoCloseIntStreamWrapper implements AutoCloseIntStream {
	private final IntStream stream;

	public AutoCloseIntStreamWrapper(IntStream stream) {
		this.stream = stream;
	}

	@Override
	public AutoCloseIntStream filter(IntPredicate predicate) {
		IntStream stream = this.stream.filter(predicate);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseIntStream map(IntUnaryOperator mapper) {
		IntStream stream = this.stream.map(mapper);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public <U> AutoCloseStream<U> mapToObj(IntFunction<? extends U> mapper) {
		Stream<U> stream = this.stream.mapToObj(mapper);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<U>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseLongStream mapToLong(IntToLongFunction mapper) {
		LongStream stream = this.stream.mapToLong(mapper);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream mapToDouble(IntToDoubleFunction mapper) {
		DoubleStream stream = this.stream.mapToDouble(mapper);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseIntStream flatMap(IntFunction<? extends IntStream> mapper) {
		IntStream stream = this.stream.flatMap(mapper);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseIntStream distinct() {
		IntStream stream = this.stream.distinct();
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseIntStream sorted() {
		IntStream stream = this.stream.sorted();
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseIntStream peek(IntConsumer action) {
		IntStream stream = this.stream.peek(action);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseIntStream limit(long maxSize) {
		IntStream stream = this.stream.limit(maxSize);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseIntStream skip(long n) {
		IntStream stream = this.stream.skip(n);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public void forEach(IntConsumer action) {
		try {
			this.stream.forEach(action);
		} finally {
			close();
		}
	}

	@Override
	public void forEachOrdered(IntConsumer action) {
		try {
			this.stream.forEachOrdered(action);
		} finally {
			close();
		}
	}

	@Override
	public int[] toArray() {
		try {
			return this.stream.toArray();
		} finally {
			close();
		}
	}

	@Override
	public int reduce(int identity, IntBinaryOperator op) {
		try {
			return this.stream.reduce(identity, op);
		} finally {
			close();
		}
	}

	@Override
	public OptionalInt reduce(IntBinaryOperator op) {
		try {
			return this.stream.reduce(op);
		} finally {
			close();
		}
	}

	@Override
	public <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		try {
			return this.stream.collect(supplier, accumulator, combiner);
		} finally {
			close();
		}
	}

	@Override
	public int sum() {
		try {
			return this.stream.sum();
		} finally {
			close();
		}
	}

	@Override
	public OptionalInt min() {
		try {
			return this.stream.min();
		} finally {
			close();
		}
	}

	@Override
	public OptionalInt max() {
		try {
			return this.stream.max();
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
	public OptionalDouble average() {
		try {
			return this.stream.average();
		} finally {
			close();
		}
	}

	@Override
	public IntSummaryStatistics summaryStatistics() {
		try {
			return this.stream.summaryStatistics();
		} finally {
			close();
		}
	}

	@Override
	public boolean anyMatch(IntPredicate predicate) {
		try {
			return this.stream.anyMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean allMatch(IntPredicate predicate) {
		try {
			return this.stream.allMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean noneMatch(IntPredicate predicate) {
		try {
			return this.stream.noneMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public OptionalInt findFirst() {
		try {
			return this.stream.findFirst();
		} finally {
			close();
		}
	}

	@Override
	public OptionalInt findAny() {
		try {
			return this.stream.findAny();
		} finally {
			close();
		}
	}

	@Override
	public LongStream asLongStream() {
		LongStream stream = this.stream.asLongStream();
		if (stream instanceof AutoCloseLongStream) {
			return stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public DoubleStream asDoubleStream() {
		DoubleStream stream = this.stream.asDoubleStream();
		if (stream instanceof AutoCloseDoubleStream) {
			return stream;
		}

		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseStream<Integer> boxed() {
		Stream<Integer> stream = this.stream.boxed();
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<Integer>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseIntStream sequential() {
		IntStream stream = this.stream.sequential();
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseIntStream parallel() {
		IntStream stream = this.stream.parallel();
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public OfInt iterator() {
		return this.stream.iterator();
	}

	@Override
	public java.util.Spliterator.OfInt spliterator() {
		return this.stream.spliterator();
	}

	@Override
	public boolean isParallel() {
		return this.stream.isParallel();
	}

	@Override
	public AutoCloseIntStream unordered() {
		IntStream stream = this.stream.unordered();
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseIntStream onClose(Runnable closeHandler) {
		IntStream stream = this.stream.onClose(closeHandler);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public void close() {
		this.stream.close();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof AutoCloseIntStreamWrapper) {
			return ObjectUtils.nullSafeEquals(((AutoCloseIntStreamWrapper) obj).stream, this.stream);
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
