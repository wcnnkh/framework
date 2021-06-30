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

	public static <T, E extends Throwable> AutoCloseStreamProcessor<T, E> autoClose(
			StreamProcessor<T, E> streamProcessor) {
		if (streamProcessor instanceof AutoCloseStreamProcessor) {
			return (AutoCloseStreamProcessor<T, E>) streamProcessor;
		}

		return new AutoCloseStreamProcessorWrapper<>(streamProcessor);
	}

	public static AutoCloseIntStream autoClose(IntStream stream) {
		if (stream instanceof AutoCloseIntStream) {
			return (AutoCloseIntStream) stream;
		}
		return new AutoCloseIntStreamWrapper(stream);
	}

	public static AutoCloseLongStream autoClose(LongStream stream) {
		if (stream instanceof AutoCloseLongStream) {
			return (AutoCloseLongStream) stream;
		}
		return new AutoCloseLongStreamWrapper(stream);
	}

	public static AutoCloseDoubleStream autoClose(DoubleStream stream) {
		if (stream instanceof AutoCloseDoubleStream) {
			return (AutoCloseDoubleStream) stream;
		}
		return new AutoCloseDoubleStreamWrapper(stream);
	}

	public static <T> AutoCloseStream<T> autoClose(Stream<T> stream) {
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}

	public static <T> AutoCloseStream<T> stream(Iterator<T> iterator) {
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
	@SuppressWarnings("resource")
	public static <T> Cursor<T> cursor(Stream<T> stream) {
		if (stream instanceof Cursor) {
			return (Cursor<T>) stream;
		}
		return new Cursor<>(stream.iterator()).onClose(() -> stream.close());
	}
}
