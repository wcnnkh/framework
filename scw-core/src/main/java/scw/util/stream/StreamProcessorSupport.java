package scw.util.stream;

import java.util.Iterator;
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

	public static <T> AutoCloseStream<T> autoClose(Stream<T> stream) {
		return new AutoCloseStreamWrapper<>(stream);
	}

	public static AutoCloseIntStream autoClose(IntStream stream) {
		return new AutoCloseIntStreamWrapper(stream);
	}

	public static AutoCloseLongStream autoClose(LongStream stream) {
		return new AutoCloseLongStreamWrapper(stream);
	}

	public static AutoCloseDoubleStream autoClose(DoubleStream stream) {
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	public static <T> AutoCloseStream<T> stream(Iterator<T> iterator) {
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
		Stream<T> stream = StreamSupport.stream(spliterator, false);
		return autoClose(stream);
	}
}
