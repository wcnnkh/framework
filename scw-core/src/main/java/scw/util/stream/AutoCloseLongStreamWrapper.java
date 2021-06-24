package scw.util.stream;

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

import scw.core.utils.ObjectUtils;

public class AutoCloseLongStreamWrapper implements AutoCloseLongStream {
	private final LongStream stream;

	public AutoCloseLongStreamWrapper(LongStream stream) {
		this.stream = stream;
	}

	@Override
	public AutoCloseLongStream filter(LongPredicate predicate) {
		LongStream stream = this.stream.filter(predicate);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream map(LongUnaryOperator mapper) {
		LongStream stream = this.stream.map(mapper);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public <U> AutoCloseStream<U> mapToObj(LongFunction<? extends U> mapper) {
		Stream<U> stream = this.stream.mapToObj(mapper);
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<U>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseIntStream mapToInt(LongToIntFunction mapper) {
		IntStream stream = this.stream.mapToInt(mapper);
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	@Override
	public AutoCloseDoubleStream mapToDouble(LongToDoubleFunction mapper) {
		DoubleStream stream = this.stream.mapToDouble(mapper);
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream flatMap(LongFunction<? extends LongStream> mapper) {
		LongStream stream = this.stream.flatMap(mapper);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream distinct() {
		LongStream stream = this.stream.distinct();
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream sorted() {
		LongStream stream = this.stream.sorted();
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream peek(LongConsumer action) {
		LongStream stream = this.stream.peek(action);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream limit(long maxSize) {
		LongStream stream = this.stream.limit(maxSize);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream skip(long n) {
		LongStream stream = this.stream.skip(n);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public void forEach(LongConsumer action) {
		try {
			this.stream.forEach(action);
		} finally {
			close();
		}
	}

	@Override
	public void forEachOrdered(LongConsumer action) {
		try {
			this.stream.forEachOrdered(action);
		} finally {
			close();
		}
	}

	@Override
	public long[] toArray() {
		try {
			return this.stream.toArray();
		} finally {
			close();
		}
	}

	@Override
	public long reduce(long identity, LongBinaryOperator op) {
		try {
			return this.stream.reduce(identity, op);
		} finally {
			close();
		}
	}

	@Override
	public OptionalLong reduce(LongBinaryOperator op) {
		try {
			return this.stream.reduce(op);
		} finally {
			close();
		}
	}

	@Override
	public <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		try {
			return this.stream.collect(supplier, accumulator, combiner);
		} finally {
			close();
		}
	}

	@Override
	public long sum() {
		try {
			return this.stream.sum();
		} finally {
			close();
		}
	}

	@Override
	public OptionalLong min() {
		try {
			return this.stream.min();
		} finally {
			close();
		}
	}

	@Override
	public OptionalLong max() {
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
	public LongSummaryStatistics summaryStatistics() {
		try {
			return this.stream.summaryStatistics();
		} finally {
			close();
		}
	}

	@Override
	public boolean anyMatch(LongPredicate predicate) {
		try {
			return this.stream.anyMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean allMatch(LongPredicate predicate) {
		try {
			return this.stream.allMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public boolean noneMatch(LongPredicate predicate) {
		try {
			return this.noneMatch(predicate);
		} finally {
			close();
		}
	}

	@Override
	public OptionalLong findFirst() {
		try {
			return this.stream.findFirst();
		} finally {
			close();
		}
	}

	@Override
	public OptionalLong findAny() {
		try {
			return this.stream.findAny();
		} finally {
			close();
		}
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
	public AutoCloseStream<Long> boxed() {
		Stream<Long> stream = this.stream.boxed();
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<Long>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	@Override
	public AutoCloseLongStream sequential() {
		LongStream stream = this.stream.sequential();
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream parallel() {
		LongStream stream = this.stream.parallel();
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public OfLong iterator() {
		return this.stream.iterator();
	}

	@Override
	public java.util.Spliterator.OfLong spliterator() {
		return this.stream.spliterator();
	}

	@Override
	public boolean isParallel() {
		return this.stream.isParallel();
	}

	@Override
	public AutoCloseLongStream unordered() {
		LongStream stream = this.stream.unordered();
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	@Override
	public AutoCloseLongStream onClose(Runnable closeHandler) {
		LongStream stream = this.stream.onClose(closeHandler);
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
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

		if (obj instanceof AutoCloseLongStreamWrapper) {
			return ObjectUtils.nullSafeEquals(((AutoCloseLongStreamWrapper) obj).stream, this.stream);
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
