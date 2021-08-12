package scw.util.stream;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import scw.lang.NotSupportedException;

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

	public static <T, E extends Throwable> AutoCloseStreamProcessor<T, E> autoClose(
			StreamProcessor<T, E> streamProcessor) {
		if (streamProcessor instanceof AutoCloseStreamProcessor) {
			return (AutoCloseStreamProcessor<T, E>) streamProcessor;
		}

		return new AutoCloseStreamProcessorWrapper<>(streamProcessor);
	}

	/**
	 * 使用静态代理而不动态代理的原因是考虑性能
	 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法
	 * 
	 * @param stream
	 * @return
	 */
	public static IntStream autoClose(IntStream stream) {
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStream(stream);
	}

	/**
	 * 使用静态代理而不动态代理的原因是考虑性能
	 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法
	 * 
	 * @param stream
	 * @return
	 */
	public static LongStream autoClose(LongStream stream) {
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStream(stream);
	}

	/**
	 * 使用静态代理而不动态代理的原因是考虑性能
	 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法
	 * 
	 * @param stream
	 * @return
	 */
	public static DoubleStream autoClose(DoubleStream stream) {
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStream(stream);
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
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>) stream;
		}
		return new AutoCloseStream<>(stream);
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
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
		Stream<T> stream = StreamSupport.stream(spliterator, false);
		return autoClose(stream);
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

	public static <T> AutoCloseStream<T> emptyAutoCloseStream() {
		return new AutoCloseStream<>(emptyStream());
	}
}