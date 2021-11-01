package io.basc.framework.util.stream;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.XUtils;

public final class StreamProcessorSupport {
	private StreamProcessorSupport() {
		throw new NotSupportedException(StreamProcessorSupport.class.getName());
	}

	public static <T, E extends Throwable> StreamProcessor<T, E> stream(CallableProcessor<T, E> processor) {
		return new DefaultStreamProcessor<T, E>(processor);
	}

	public static <T, E extends Throwable> StreamProcessor<T, E> stream(T source) {
		return stream(() -> {
			return source;
		});
	}

	/**
	 * 使用静态代理而不动态代理的原因是考虑性能
	 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法
	 * 
	 * @param stream
	 * @return
	 */
	public static IntStream autoClose(IntStream stream) {
		if (stream instanceof IntStreamWrapper) {
			return (IntStreamWrapper) stream;
		}
		return new IntStreamWrapper(stream);
	}

	/**
	 * 使用静态代理而不动态代理的原因是考虑性能
	 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法
	 * 
	 * @param stream
	 * @return
	 */
	public static LongStream autoClose(LongStream stream) {
		if (stream instanceof LongStreamWrapper) {
			return (LongStreamWrapper) stream;
		}
		return new LongStreamWrapper(stream);
	}

	/**
	 * 使用静态代理而不动态代理的原因是考虑性能
	 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法
	 * 
	 * @param stream
	 * @return
	 */
	public static DoubleStream autoClose(DoubleStream stream) {
		if (stream instanceof DoubleStreamWrapper) {
			return (DoubleStreamWrapper) stream;
		}
		return new DoubleStreamWrapper(stream);
	}

	/**
	 * 使用静态代理而不动态代理的原因是考虑性能
	 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法
	 * 
	 * @param <T>
	 * @param stream
	 * @return
	 */
	public static <T> Stream<T> autoClose(Stream<T> stream) {
		if (stream instanceof StreamWrapper) {
			return (StreamWrapper<T>) stream;
		}
		return new StreamWrapper<>(stream);
	}

	/**
	 * 使用静态代理而不动态代理的原因是考虑性能
	 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法
	 * 
	 * @param <T>
	 * @param iterator
	 * @return
	 */
	public static <T> Stream<T> stream(Iterator<T> iterator) {
		return autoClose(XUtils.stream(iterator));
	}

	public static <T> Cursor<T> cursor(Iterator<T> iterator) {
		return new Cursor<>(iterator);
	}

	/**
	 * @see Cursor
	 * @param <T>
	 * @param stream
	 * @return
	 */
	public static <T> Cursor<T> cursor(Stream<T> stream) {
		if (stream instanceof Cursor) {
			return (Cursor<T>) stream;
		}
		return new Cursor<>(stream);
	}

	public static <T> Cursor<T> emptyCursor() {
		return new Cursor<>(Collections.emptyIterator());
	}

	public static <T> Stream<T> emptyStream() {
		List<T> list = Collections.emptyList();
		return list.stream();
	}

	public static <T> StreamWrapper<T> emptyAutoCloseStream() {
		return new StreamWrapper<>(emptyStream());
	}
}
