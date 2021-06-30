package scw.util.stream;

import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator.OfDouble;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import scw.util.Wrapper;

public class AutoCloseDoubleStreamWrapper extends Wrapper<DoubleStream> implements AutoCloseDoubleStream {
	private boolean closed;

	public AutoCloseDoubleStreamWrapper(DoubleStream stream) {
		super(stream);
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		this.closed = true;
		wrappedTarget.close();
	}

	@Override
	public AutoCloseDoubleStream filter(DoublePredicate predicate) {
		DoubleStream stream = this.wrappedTarget.filter(predicate);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream map(DoubleUnaryOperator mapper) {
		DoubleStream stream = this.wrappedTarget.map(mapper);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public <U> AutoCloseStream<U> mapToObj(DoubleFunction<? extends U> mapper) {
		Stream<U> stream = this.wrappedTarget.mapToObj(mapper);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<U>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseIntStream mapToInt(DoubleToIntFunction mapper) {
		IntStream stream = this.wrappedTarget.mapToInt(mapper);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream mapToLong(DoubleToLongFunction mapper) {
		LongStream stream = this.wrappedTarget.mapToLong(mapper);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper) {
		DoubleStream stream = this.wrappedTarget.flatMap(mapper);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream distinct() {
		DoubleStream stream = this.wrappedTarget.distinct();
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream sorted() {
		DoubleStream stream = this.wrappedTarget.sorted();
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream peek(DoubleConsumer action) {
		DoubleStream stream = this.wrappedTarget.peek(action);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream limit(long maxSize) {
		DoubleStream stream = this.wrappedTarget.limit(maxSize);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream skip(long n) {
		DoubleStream stream = this.wrappedTarget.skip(n);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public void forEach(DoubleConsumer action) {
		try {
			this.wrappedTarget.forEach(action);
		} finally {
			close();
		}
	}

	@Override
	public void forEachOrdered(DoubleConsumer action) {
		try {
			this.wrappedTarget.forEachOrdered(action);
		} finally {
			close();
		}
	}

	@Override
	public double[] toArray() {
		try {
			return this.wrappedTarget.toArray();
		} finally {
			close();
		}
	}

	@Override
	public double reduce(double identity, DoubleBinaryOperator op) {
		try {
			return this.wrappedTarget.reduce(identity, op);
		} finally {
			close();
		}
	}

	@Override
	public OptionalDouble reduce(DoubleBinaryOperator op) {
		try {
			return this.wrappedTarget.reduce(op);
		} finally {
			close();
		}
	}

	@Override
	public <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		try {
			return this.wrappedTarget.collect(supplier, accumulator, combiner);
		} finally {
			close();
		}
	}

	@Override
	public double sum() {
		try {
			return this.wrappedTarget.sum();
		} finally {
			close();
		}
	}

	@Override
	public OptionalDouble min() {
		try {
			return this.wrappedTarget.min();
		} finally {
			close();
		}
	}

	@Override
	public OptionalDouble max() {
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
	public DoubleSummaryStatistics summaryStatistics() {
		try {
			return this.wrappedTarget.summaryStatistics();
		} finally {
			close();
		}
	}

	@Override
	public boolean anyMatch(DoublePredicate predicate) {
		try {
			return this.wrappedTarget.anyMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean allMatch(DoublePredicate predicate) {
		try {
			return this.wrappedTarget.allMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean noneMatch(DoublePredicate predicate) {
		try {
			return this.wrappedTarget.noneMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public OptionalDouble findFirst() {
		try {
			return this.wrappedTarget.findFirst();
		} finally {
			close();
		}
	}

	@Override
	public OptionalDouble findAny() {
		try {
			return this.wrappedTarget.findAny();
		} finally {
			close();
		}
	}

	@Override
	public AutoCloseStream<Double> boxed() {
		Stream<Double> stream = this.wrappedTarget.boxed();
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<Double>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseDoubleStream sequential() {
		DoubleStream stream = this.wrappedTarget.sequential();
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream parallel() {
		DoubleStream stream = this.wrappedTarget.parallel();
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public OfDouble iterator() {
		return this.wrappedTarget.iterator();
	}

	@Override
	public java.util.Spliterator.OfDouble spliterator() {
		return this.wrappedTarget.spliterator();
	}

	@Override
	public boolean isParallel() {
		return this.wrappedTarget.isParallel();
	}

	@Override
	public AutoCloseDoubleStream unordered() {
		DoubleStream stream = this.wrappedTarget.unordered();
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream onClose(Runnable closeHandler) {
		DoubleStream stream = this.wrappedTarget.onClose(closeHandler);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}
}
