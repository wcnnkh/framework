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

import scw.util.Wrapper;

public class AutoCloseIntStream extends Wrapper<IntStream> implements IntStream {
	
	public AutoCloseIntStream(IntStream stream) {
		super(stream);
	}
	
	@Override
	public void close() {
		wrappedTarget.close();
	}

	@Override
	public AutoCloseIntStream filter(IntPredicate predicate) {
		IntStream stream = this.wrappedTarget.filter(predicate);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public AutoCloseIntStream map(IntUnaryOperator mapper) {
		IntStream stream = this.wrappedTarget.map(mapper);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public <U> AutoCloseStream<U> mapToObj(IntFunction<? extends U> mapper) {
		Stream<U> stream = this.wrappedTarget.mapToObj(mapper);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<U>) stream;
		}
		return new AutoCloseStream<>(stream);
	}

	@Override
	public AutoCloseLongStream mapToLong(IntToLongFunction mapper) {
		LongStream stream = this.wrappedTarget.mapToLong(mapper);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStream(stream);
	}

	@Override
	public AutoCloseDoubleStream mapToDouble(IntToDoubleFunction mapper) {
		DoubleStream stream = this.wrappedTarget.mapToDouble(mapper);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStream(stream);
	}

	@Override
	public AutoCloseIntStream flatMap(IntFunction<? extends IntStream> mapper) {
		IntStream stream = this.wrappedTarget.flatMap(mapper);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public AutoCloseIntStream distinct() {
		IntStream stream = this.wrappedTarget.distinct();
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public AutoCloseIntStream sorted() {
		IntStream stream = this.wrappedTarget.sorted();
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public AutoCloseIntStream peek(IntConsumer action) {
		IntStream stream = this.wrappedTarget.peek(action);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public AutoCloseIntStream limit(long maxSize) {
		IntStream stream = this.wrappedTarget.limit(maxSize);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public AutoCloseIntStream skip(long n) {
		IntStream stream = this.wrappedTarget.skip(n);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public void forEach(IntConsumer action) {
		try {
			this.wrappedTarget.forEach(action);
		} finally {
			close();
		}
	}

	@Override
	public void forEachOrdered(IntConsumer action) {
		try {
			this.wrappedTarget.forEachOrdered(action);
		} finally {
			close();
		}
	}

	@Override
	public int[] toArray() {
		try {
			return this.wrappedTarget.toArray();
		} finally {
			close();
		}
	}

	@Override
	public int reduce(int identity, IntBinaryOperator op) {
		try {
			return this.wrappedTarget.reduce(identity, op);
		} finally {
			close();
		}
	}

	@Override
	public OptionalInt reduce(IntBinaryOperator op) {
		try {
			return this.wrappedTarget.reduce(op);
		} finally {
			close();
		}
	}

	@Override
	public <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		try {
			return this.wrappedTarget.collect(supplier, accumulator, combiner);
		} finally {
			close();
		}
	}

	@Override
	public int sum() {
		try {
			return this.wrappedTarget.sum();
		} finally {
			close();
		}
	}

	@Override
	public OptionalInt min() {
		try {
			return this.wrappedTarget.min();
		} finally {
			close();
		}
	}

	@Override
	public OptionalInt max() {
		try {
			return this.wrappedTarget.max();
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
	public OptionalDouble average() {
		try {
			return this.wrappedTarget.average();
		} finally {
			close();
		}
	}

	@Override
	public IntSummaryStatistics summaryStatistics() {
		try {
			return this.wrappedTarget.summaryStatistics();
		} finally {
			close();
		}
	}

	@Override
	public boolean anyMatch(IntPredicate predicate) {
		try {
			return this.wrappedTarget.anyMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean allMatch(IntPredicate predicate) {
		try {
			return this.wrappedTarget.allMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean noneMatch(IntPredicate predicate) {
		try {
			return this.wrappedTarget.noneMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public OptionalInt findFirst() {
		try {
			return this.wrappedTarget.findFirst();
		} finally {
			close();
		}
	}

	@Override
	public OptionalInt findAny() {
		try {
			return this.wrappedTarget.findAny();
		} finally {
			close();
		}
	}

	@Override
	public LongStream asLongStream() {
		LongStream stream = this.wrappedTarget.asLongStream();
		if (stream instanceof AutoCloseLongStream) {
			return stream;
		}
		return new AutoCloseLongStream(stream);
	}

	@Override
	public DoubleStream asDoubleStream() {
		DoubleStream stream = this.wrappedTarget.asDoubleStream();
		if (stream instanceof AutoCloseDoubleStream) {
			return stream;
		}

		return new AutoCloseDoubleStream(stream);
	}

	@Override
	public AutoCloseStream<Integer> boxed() {
		Stream<Integer> stream = this.wrappedTarget.boxed();
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<Integer>) stream;
		}
		return new AutoCloseStream<>(stream);
	}

	@Override
	public AutoCloseIntStream sequential() {
		IntStream stream = this.wrappedTarget.sequential();
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public AutoCloseIntStream parallel() {
		IntStream stream = this.wrappedTarget.parallel();
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public OfInt iterator() {
		return this.wrappedTarget.iterator();
	}

	@Override
	public java.util.Spliterator.OfInt spliterator() {
		return this.wrappedTarget.spliterator();
	}

	@Override
	public boolean isParallel() {
		return this.wrappedTarget.isParallel();
	}

	@Override
	public AutoCloseIntStream unordered() {
		IntStream stream = this.wrappedTarget.unordered();
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	@Override
	public AutoCloseIntStream onClose(Runnable closeHandler) {
		IntStream stream = this.wrappedTarget.onClose(closeHandler);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}
}
