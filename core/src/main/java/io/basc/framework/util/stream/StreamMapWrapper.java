package io.basc.framework.util.stream;

import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class StreamMapWrapper<T, S extends Stream<T>> extends AbstractStreamWrapper<T, S>
		implements Stream<T> {

	public StreamMapWrapper(Stream<T> stream) {
		super(stream);
	}

	/**
	 * 尽可能重写此方法以达到重复使用的目的
	 * @see Cursor#map(Function)
	 */
	@Override
	public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
		Stream<R> stream = super.map(mapper);
		if (stream instanceof StreamWrapper) {
			return (StreamWrapper<R>) stream;
		}

		return new StreamWrapper<>(stream);
	}

	@Override
	public IntStreamWrapper mapToInt(ToIntFunction<? super T> mapper) {
		IntStream stream = this.wrappedTarget.mapToInt(mapper);
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public LongStreamWrapper mapToLong(ToLongFunction<? super T> mapper) {
		LongStream stream = this.wrappedTarget.mapToLong(mapper);
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper mapToDouble(ToDoubleFunction<? super T> mapper) {
		DoubleStream stream = this.wrappedTarget.mapToDouble(mapper);
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	@Override
	public <R> StreamWrapper<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		Stream<R> stream = this.wrappedTarget.flatMap(mapper);
		if (stream instanceof StreamWrapper) {
			return (StreamWrapper<R>) stream;
		}
		return new StreamWrapper<>(stream);
	}

	@Override
	public IntStreamWrapper flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		IntStream stream = this.wrappedTarget.flatMapToInt(mapper);
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	@Override
	public LongStreamWrapper flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		LongStream stream = this.wrappedTarget.flatMapToLong(mapper);
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	@Override
	public DoubleStreamWrapper flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		DoubleStream stream = this.wrappedTarget.flatMapToDouble(mapper);
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

}
