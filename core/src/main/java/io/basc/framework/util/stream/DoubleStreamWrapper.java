package io.basc.framework.util.stream;

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

public class DoubleStreamWrapper extends BaseStreamWrapper<Double, DoubleStream> implements DoubleStream {

	public DoubleStreamWrapper(DoubleStream stream) {
		super(stream);
	}

	@Override
	public DoubleStreamWrapper filter(DoublePredicate predicate) {
		DoubleStream stream = this.wrappedTarget.filter(predicate);
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper map(DoubleUnaryOperator mapper) {
		DoubleStream stream = this.wrappedTarget.map(mapper);
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public <U> StreamWrapper<U> mapToObj(DoubleFunction<? extends U> mapper) {
		Stream<U> stream = this.wrappedTarget.mapToObj(mapper);
		if (stream instanceof StreamWrapper) {
			return (StreamWrapper<U>) stream;
		}
		return new StreamWrapper<>(stream);
	}

	@Override
	public IntStreamWrapper mapToInt(DoubleToIntFunction mapper) {
		IntStream stream = this.wrappedTarget.mapToInt(mapper);
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public LongStreamWrapper mapToLong(DoubleToLongFunction mapper) {
		LongStream stream = this.wrappedTarget.mapToLong(mapper);
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper flatMap(DoubleFunction<? extends DoubleStream> mapper) {
		DoubleStream stream = this.wrappedTarget.flatMap(mapper);
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper distinct() {
		DoubleStream stream = this.wrappedTarget.distinct();
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper sorted() {
		DoubleStream stream = this.wrappedTarget.sorted();
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper peek(DoubleConsumer action) {
		DoubleStream stream = this.wrappedTarget.peek(action);
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper limit(long maxSize) {
		DoubleStream stream = this.wrappedTarget.limit(maxSize);
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper skip(long n) {
		DoubleStream stream = this.wrappedTarget.skip(n);
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public void forEach(DoubleConsumer action) {
		try {
			beforeExecution();
			this.wrappedTarget.forEach(action);
		} finally {
			afterExecution();
		}
	}

	@Override
	public void forEachOrdered(DoubleConsumer action) {
		try {
			beforeExecution();
			this.wrappedTarget.forEachOrdered(action);
		} finally {
			afterExecution();
		}
	}

	@Override
	public double[] toArray() {
		try {
			beforeExecution();
			return this.wrappedTarget.toArray();
		} finally {
			afterExecution();
		}
	}

	@Override
	public double reduce(double identity, DoubleBinaryOperator op) {
		try {
			beforeExecution();
			return this.wrappedTarget.reduce(identity, op);
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalDouble reduce(DoubleBinaryOperator op) {
		try {
			beforeExecution();
			return this.wrappedTarget.reduce(op);
		} finally {
			afterExecution();
		}
	}

	@Override
	public <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		try {
			beforeExecution();
			return this.wrappedTarget.collect(supplier, accumulator, combiner);
		} finally {
			afterExecution();
		}
	}

	@Override
	public double sum() {
		try {
			beforeExecution();
			return this.wrappedTarget.sum();
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalDouble min() {
		try {
			beforeExecution();
			return this.wrappedTarget.min();
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalDouble max() {
		try {
			beforeExecution();
			return this.wrappedTarget.max();
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
	public OptionalDouble average() {
		try {
			beforeExecution();
			return this.wrappedTarget.average();
		} finally {
			afterExecution();
		}
	}

	@Override
	public DoubleSummaryStatistics summaryStatistics() {
		try {
			beforeExecution();
			return this.wrappedTarget.summaryStatistics();
		} finally {
			afterExecution();
		}
	}

	@Override
	public boolean anyMatch(DoublePredicate predicate) {
		try {
			beforeExecution();
			return this.wrappedTarget.anyMatch(predicate);
		} finally {
			afterExecution();
		}
	}

	@Override
	public boolean allMatch(DoublePredicate predicate) {
		try {
			beforeExecution();
			return this.wrappedTarget.allMatch(predicate);
		} finally {
			afterExecution();
		}
	}

	@Override
	public boolean noneMatch(DoublePredicate predicate) {
		try {
			beforeExecution();
			return this.wrappedTarget.noneMatch(predicate);
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalDouble findFirst() {
		try {
			beforeExecution();
			return this.wrappedTarget.findFirst();
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalDouble findAny() {
		try {
			beforeExecution();
			return this.wrappedTarget.findAny();
		} finally {
			afterExecution();
		}
	}

	@Override
	public StreamWrapper<Double> boxed() {
		Stream<Double> stream = this.wrappedTarget.boxed();
		if (stream instanceof StreamWrapper) {
			return (StreamWrapper<Double>) stream;
		}
		return new StreamWrapper<>(stream);
	}

	@Override
	public DoubleStreamWrapper sequential() {
		DoubleStream stream = this.wrappedTarget.sequential();
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper parallel() {
		DoubleStream stream = this.wrappedTarget.parallel();
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
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
	public DoubleStreamWrapper unordered() {
		DoubleStream stream = this.wrappedTarget.unordered();
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper onClose(Runnable closeHandler) {
		DoubleStream stream = this.wrappedTarget.onClose(closeHandler);
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}
}
