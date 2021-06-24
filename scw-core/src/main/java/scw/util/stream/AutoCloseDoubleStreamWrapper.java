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

import scw.core.utils.ObjectUtils;

public class AutoCloseDoubleStreamWrapper implements AutoCloseDoubleStream {
	private final DoubleStream stream;

	public AutoCloseDoubleStreamWrapper(DoubleStream stream) {
		this.stream = stream;
	}

	@Override
	public AutoCloseDoubleStream filter(DoublePredicate predicate) {
		DoubleStream stream = this.stream.filter(predicate);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream map(DoubleUnaryOperator mapper) {
		DoubleStream stream = this.stream.map(mapper);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public <U> AutoCloseStream<U> mapToObj(DoubleFunction<? extends U> mapper) {
		Stream<U> stream = this.stream.mapToObj(mapper);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<U>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseIntStream mapToInt(DoubleToIntFunction mapper) {
		IntStream stream = this.stream.mapToInt(mapper);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream mapToLong(DoubleToLongFunction mapper) {
		LongStream stream = this.stream.mapToLong(mapper);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper) {
		DoubleStream stream = this.stream.flatMap(mapper);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream distinct() {
		DoubleStream stream = this.stream.distinct();
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream sorted() {
		DoubleStream stream = this.stream.sorted();
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream peek(DoubleConsumer action) {
		DoubleStream stream = this.stream.peek(action);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream limit(long maxSize) {
		DoubleStream stream = this.stream.limit(maxSize);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream skip(long n) {
		DoubleStream stream = this.stream.skip(n);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public void forEach(DoubleConsumer action) {
		try {
			this.stream.forEach(action);
		} finally {
			close();
		}
	}

	@Override
	public void forEachOrdered(DoubleConsumer action) {
		try {
			this.stream.forEachOrdered(action);
		} finally {
			close();
		}
	}

	@Override
	public double[] toArray() {
		try {
			return this.stream.toArray();
		} finally {
			close();
		}
	}

	@Override
	public double reduce(double identity, DoubleBinaryOperator op) {
		try {
			return this.stream.reduce(identity, op);
		} finally {
			close();
		}
	}

	@Override
	public OptionalDouble reduce(DoubleBinaryOperator op) {
		try {
			return this.stream.reduce(op);
		} finally {
			close();
		}
	}

	@Override
	public <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		try {
			return this.stream.collect(supplier, accumulator, combiner);
		} finally {
			close();
		}
	}

	@Override
	public double sum() {
		try {
			return this.stream.sum();
		} finally {
			close();
		}
	}

	@Override
	public OptionalDouble min() {
		try {
			return this.stream.min();
		} finally {
			close();
		}
	}

	@Override
	public OptionalDouble max() {
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
	public DoubleSummaryStatistics summaryStatistics() {
		try {
			return this.stream.summaryStatistics();
		} finally {
			close();
		}
	}

	@Override
	public boolean anyMatch(DoublePredicate predicate) {
		try {
			return this.stream.anyMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean allMatch(DoublePredicate predicate) {
		try {
			return this.stream.allMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean noneMatch(DoublePredicate predicate) {
		try {
			return this.stream.noneMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public OptionalDouble findFirst() {
		try {
			return this.stream.findFirst();
		} finally {
			close();
		}
	}

	@Override
	public OptionalDouble findAny() {
		try {
			return this.stream.findAny();
		} finally {
			close();
		}
	}

	@Override
	public AutoCloseStream<Double> boxed() {
		Stream<Double> stream = this.stream.boxed();
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<Double>)stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseDoubleStream sequential() {
		DoubleStream stream = this.stream.sequential();
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream parallel() {
		DoubleStream stream = this.stream.parallel();
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public OfDouble iterator() {
		return this.stream.iterator();
	}

	@Override
	public java.util.Spliterator.OfDouble spliterator() {
		return this.stream.spliterator();
	}

	@Override
	public boolean isParallel() {
		return this.stream.isParallel();
	}

	@Override
	public AutoCloseDoubleStream unordered() {
		DoubleStream stream = this.stream.unordered();
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream onClose(Runnable closeHandler) {
		DoubleStream stream = this.stream.onClose(closeHandler);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
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

		if (obj instanceof AutoCloseDoubleStreamWrapper) {
			return ObjectUtils.nullSafeEquals(((AutoCloseDoubleStreamWrapper) obj).stream, this.stream);
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
