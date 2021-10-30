package io.basc.framework.util.stream;

import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.PrimitiveIterator.OfLong;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class LongStreamWrapper extends BaseStreamWrapper<Long, LongStream> implements LongStream {

	public LongStreamWrapper(LongStream stream) {
		super(stream);
	}

	@Override
	public LongStreamWrapper filter(LongPredicate predicate) {
		LongStream stream = this.wrappedTarget.filter(predicate);
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public LongStreamWrapper map(LongUnaryOperator mapper) {
		LongStream stream = this.wrappedTarget.map(mapper);
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public <U> StreamWrapper<U> mapToObj(LongFunction<? extends U> mapper) {
		Stream<U> stream = this.wrappedTarget.mapToObj(mapper);
		if (stream instanceof StreamWrapper) {
			return (StreamWrapper<U>) stream;
		}
		return new StreamWrapper<>(stream);
	}

	@Override
	public IntStreamWrapper mapToInt(LongToIntFunction mapper) {
		IntStream stream = this.wrappedTarget.mapToInt(mapper);
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper mapToDouble(LongToDoubleFunction mapper) {
		DoubleStream stream = this.wrappedTarget.mapToDouble(mapper);
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public LongStreamWrapper flatMap(LongFunction<? extends LongStream> mapper) {
		LongStream stream = this.wrappedTarget.flatMap(mapper);
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public LongStreamWrapper distinct() {
		LongStream stream = this.wrappedTarget.distinct();
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public LongStreamWrapper sorted() {
		LongStream stream = this.wrappedTarget.sorted();
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public LongStreamWrapper peek(LongConsumer action) {
		LongStream stream = this.wrappedTarget.peek(action);
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public LongStreamWrapper limit(long maxSize) {
		LongStream stream = this.wrappedTarget.limit(maxSize);
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public LongStreamWrapper skip(long n) {
		LongStream stream = this.wrappedTarget.skip(n);
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public void forEach(LongConsumer action) {
		try {
			beforeExecution();
			this.wrappedTarget.forEach(action);
		} finally {
			afterExecution();
		}
	}

	@Override
	public void forEachOrdered(LongConsumer action) {
		try {
			beforeExecution();
			this.wrappedTarget.forEachOrdered(action);
		} finally {
			afterExecution();
		}
	}

	@Override
	public long[] toArray() {
		try {
			beforeExecution();
			return this.wrappedTarget.toArray();
		} finally {
			afterExecution();
		}
	}

	@Override
	public long reduce(long identity, LongBinaryOperator op) {
		try {
			beforeExecution();
			return this.wrappedTarget.reduce(identity, op);
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalLong reduce(LongBinaryOperator op) {
		try {
			beforeExecution();
			return this.wrappedTarget.reduce(op);
		} finally {
			afterExecution();
		}
	}

	@Override
	public <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		try {
			beforeExecution();
			return this.wrappedTarget.collect(supplier, accumulator, combiner);
		} finally {
			afterExecution();
		}
	}

	@Override
	public long sum() {
		try {
			beforeExecution();
			return this.wrappedTarget.sum();
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalLong min() {
		try {
			beforeExecution();
			return this.wrappedTarget.min();
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalLong max() {
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
	public LongSummaryStatistics summaryStatistics() {
		try {
			beforeExecution();
			return this.wrappedTarget.summaryStatistics();
		} finally {
			afterExecution();
		}
	}

	@Override
	public boolean anyMatch(LongPredicate predicate) {
		try {
			beforeExecution();
			return this.wrappedTarget.anyMatch(predicate);
		} finally {
			afterExecution();
		}
	}

	@Override
	public boolean allMatch(LongPredicate predicate) {
		try {
			beforeExecution();
			return this.wrappedTarget.allMatch(predicate);
		} finally {
			afterExecution();
		}
	}

	@Override
	public boolean noneMatch(LongPredicate predicate) {
		try {
			beforeExecution();
			return this.noneMatch(predicate);
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalLong findFirst() {
		try {
			beforeExecution();
			return this.wrappedTarget.findFirst();
		} finally {
			afterExecution();
		}
	}

	@Override
	public OptionalLong findAny() {
		try {
			beforeExecution();
			return this.wrappedTarget.findAny();
		} finally {
			afterExecution();
		}
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
	public StreamWrapper<Long> boxed() {
		Stream<Long> stream = this.wrappedTarget.boxed();
		if (stream instanceof StreamWrapper) {
			return (StreamWrapper<Long>) stream;
		}
		return new StreamWrapper<>(stream);
	}

	@Override
	public LongStreamWrapper sequential() {
		LongStream stream = this.wrappedTarget.sequential();
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public LongStreamWrapper parallel() {
		LongStream stream = this.wrappedTarget.parallel();
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public OfLong iterator() {
		return this.wrappedTarget.iterator();
	}

	@Override
	public java.util.Spliterator.OfLong spliterator() {
		return this.wrappedTarget.spliterator();
	}

	@Override
	public boolean isParallel() {
		return this.wrappedTarget.isParallel();
	}

	@Override
	public LongStreamWrapper unordered() {
		LongStream stream = this.wrappedTarget.unordered();
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public LongStreamWrapper onClose(Runnable closeHandler) {
		LongStream stream = this.wrappedTarget.onClose(closeHandler);
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public void close() {
		this.wrappedTarget.close();
	}
}
