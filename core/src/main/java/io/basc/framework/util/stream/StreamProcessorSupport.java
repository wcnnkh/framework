package io.basc.framework.util.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Pair;
import io.basc.framework.util.XUtils;

public final class StreamProcessorSupport {
	private StreamProcessorSupport() {
		throw new NotSupportedException(StreamProcessorSupport.class.getName());
	}

	public static <T, E extends Throwable> StreamProcessor<T, E> stream(
			CallableProcessor<T, E> processor) {
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

	public static <T> Stream<T> concat(Collection<? extends Stream<T>> streams) {
		if (streams == null) {
			return emptyStream();
		}

		return concat(streams.iterator());
	}

	public static <T> Stream<T> concat(Iterator<? extends Stream<T>> streams) {
		if (streams == null) {
			return emptyStream();
		}

		if (!streams.hasNext()) {
			return emptyStream();
		}

		Stream<T> stream = streams.next();
		if (!streams.hasNext()) {
			return stream;
		}

		stream = Stream.concat(stream, streams.next());
		if (!streams.hasNext()) {
			return stream;
		}

		while (streams.hasNext()) {
			stream = Stream.concat(stream, streams.next());
		}
		return stream;
	}

	public static <K, V, E extends Throwable> Optional<Pair<K, V>> process(
			Iterable<? extends K> keys, Processor<K, V, E> processor,
			Predicate<Pair<K, V>> returnTest) throws E {
		return process(
				keys == null ? Collections.emptyIterator() : keys.iterator(),
				processor, returnTest);
	}

	public static <K, V, E extends Throwable> Optional<Pair<K, V>> process(
			Iterator<? extends K> keys, Processor<K, V, E> processor,
			Predicate<Pair<K, V>> returnTest) throws E {
		Assert.requiredArgument(processor != null, "processor");
		if (keys == null) {
			return Optional.empty();
		}

		while (keys.hasNext()) {
			K key = keys.next();
			V value = processor.process(key);
			if (value == null) {
				continue;
			}

			Pair<K, V> pair = new Pair<K, V>(key, value);
			if (returnTest == null || returnTest.test(pair)) {
				return Optional.ofNullable(pair);
			}
		}
		return Optional.empty();
	}

	public static <K, V, E extends Throwable> List<Pair<K, V>> processAll(
			Iterable<? extends K> keys, Processor<K, V, E> processor,
			Predicate<Pair<K, V>> predicate) throws E {
		return processAll(
				keys == null ? Collections.emptyIterator() : keys.iterator(),
				processor, predicate);
	}

	public static <K, V, E extends Throwable> List<Pair<K, V>> processAll(
			Iterator<? extends K> keys, Processor<K, V, E> processor,
			Predicate<Pair<K, V>> predicate) throws E {
		Assert.requiredArgument(processor != null, "processor");
		Assert.requiredArgument(predicate != null, "predicate");
		if (keys == null) {
			return Collections.emptyList();
		}

		List<Pair<K, V>> list = new ArrayList<>();
		while (keys.hasNext()) {
			K key = keys.next();
			V value = processor.process(key);
			Pair<K, V> pair = new Pair<K, V>(key, value);
			if (predicate.test(pair)) {
				list.add(pair);
			}
		}
		return list.isEmpty() ? Collections.emptyList() : list;
	}
}
