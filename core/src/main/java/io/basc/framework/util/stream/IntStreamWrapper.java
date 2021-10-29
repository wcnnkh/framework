package io.basc.framework.util.stream;

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

public class IntStreamWrapper extends BaseStreamWrapper<Integer, IntStream> implements IntStream {

	public IntStreamWrapper(IntStream stream) {
		super(stream);
	}

	@Override
	public IntStreamWrapper filter(IntPredicate predicate) {
		IntStream stream = this.wrappedTarget.filter(predicate);
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public IntStreamWrapper map(IntUnaryOperator mapper) {
		IntStream stream = this.wrappedTarget.map(mapper);
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public <U> StreamWrapper<U> mapToObj(IntFunction<? extends U> mapper) {
		Stream<U> stream = this.wrappedTarget.mapToObj(mapper);
		if (stream instanceof StreamWrapper) {
			return (StreamWrapper<U>) stream;
		}
		return new StreamWrapper<>(stream);
	}

	@Override
	public LongStreamWrapper mapToLong(IntToLongFunction mapper) {
		LongStream stream = this.wrappedTarget.mapToLong(mapper);
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper mapToDouble(IntToDoubleFunction mapper) {
		DoubleStream stream = this.wrappedTarget.mapToDouble(mapper);
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public IntStreamWrapper flatMap(IntFunction<? extends IntStream> mapper) {
		IntStream stream = this.wrappedTarget.flatMap(mapper);
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public IntStreamWrapper distinct() {
		IntStream stream = this.wrappedTarget.distinct();
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public IntStreamWrapper sorted() {
		IntStream stream = this.wrappedTarget.sorted();
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public IntStreamWrapper peek(IntConsumer action) {
		IntStream stream = this.wrappedTarget.peek(action);
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public IntStreamWrapper limit(long maxSize) {
		IntStream stream = this.wrappedTarget.limit(maxSize);
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public IntStreamWrapper skip(long n) {
		IntStream stream = this.wrappedTarget.skip(n);
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public void forEach(IntConsumer action) {
		try {
			beforeExecution();
			this.wrappedTarget.forEach(action);
		} finally {
			afterExecution();
		}
	}

	@Override
	public void forEachOrdered(IntConsumer action) {
		try {
			beforeExecution();
			this.wrappedTarget.forEachOrdered(action);
		} finally {
			afterExecution();
		}
	}

	@Override
	public int[] toArray() {
		try {
			beforeExecution();
			return this.wrappedTarget.toArray();
		} finally {
			afterExecution();
		}
	}

	@Override
	public int reduce(int identity, IntBinaryOperator op) {
		try {
			beforeExecution();
			return this.wrappedTarget.reduce(identity, op);
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalInt reduce(IntBinaryOperator op) {
		try {
			beforeExecution();
			return this.wrappedTarget.reduce(op);
		} finally {
			afterExecution();
		}
	}

	@Override
	public <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		try {
			beforeExecution();
			return this.wrappedTarget.collect(supplier, accumulator, combiner);
		} finally {
			afterExecution();
		}
	}

	@Override
	public int sum() {
		try {
			beforeExecution();
			return this.wrappedTarget.sum();
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalInt min() {
		try {
			beforeExecution();
			return this.wrappedTarget.min();
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalInt max() {
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
	public IntSummaryStatistics summaryStatistics() {
		try {
			beforeExecution();
			return this.wrappedTarget.summaryStatistics();
		} finally {
			afterExecution();
		}
	}

	@Override
	public boolean anyMatch(IntPredicate predicate) {
		try {
			beforeExecution();
			return this.wrappedTarget.anyMatch(predicate);
		} finally {
			afterExecution();
		}
	}

	@Override
	public boolean allMatch(IntPredicate predicate) {
		try {
			beforeExecution();
			return this.wrappedTarget.allMatch(predicate);
		} finally {
			afterExecution();
		}
	}

	@Override
	public boolean noneMatch(IntPredicate predicate) {
		try {
			beforeExecution();
			return this.wrappedTarget.noneMatch(predicate);
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalInt findFirst() {
		try {
			beforeExecution();
			return this.wrappedTarget.findFirst();
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalInt findAny() {
		try {
			beforeExecution();
			return this.wrappedTarget.findAny();
		} finally {
			afterExecution();
		}
	}

	@Override
	public LongStream asLongStream() {
		LongStream stream = this.wrappedTarget.asLongStream();
		if (stream instanceof LongStreamWrapper) {
			return stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public DoubleStream asDoubleStream() {
		DoubleStream stream = this.wrappedTarget.asDoubleStream();
		if (stream instanceof DoubleStreamWrapper) {
			return stream;
		}

		return new DoubleStreamWrapper(stream);
	}

	@Override
	public StreamWrapper<Integer> boxed() {
		Stream<Integer> stream = this.wrappedTarget.boxed();
		if (stream instanceof StreamWrapper) {
			return (StreamWrapper<Integer>) stream;
		}
		return new StreamWrapper<>(stream);
	}

	@Override
	public IntStreamWrapper sequential() {
		IntStream stream = this.wrappedTarget.sequential();
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public IntStreamWrapper parallel() {
		IntStream stream = this.wrappedTarget.parallel();
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
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
	public IntStreamWrapper unordered() {
		IntStream stream = this.wrappedTarget.unordered();
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public IntStreamWrapper onClose(Runnable closeHandler) {
		IntStream stream = this.wrappedTarget.onClose(closeHandler);
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}
}
