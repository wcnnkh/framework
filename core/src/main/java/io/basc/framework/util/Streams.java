package io.basc.framework.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 对{@link StreamSupport}和{@link Stream}的扩展
 * 
 * @author wcnnkh
 *
 */
public class Streams {
	private static final Stream<?> EMPTY = stream(Collections.emptyIterator());

	@SuppressWarnings("unchecked")
	public static <E> Stream<E> empty() {
		return (Stream<E>) EMPTY;
	}

	/**
	 * @see Stream#concat(Stream, Stream)
	 * @param <T>
	 * @param iterator
	 * @return
	 */
	public static <T> Stream<T> concat(Iterator<? extends Stream<T>> iterator) {
		if (iterator == null || !iterator.hasNext()) {
			return Stream.empty();
		}

		Stream<T> stream = null;
		while (iterator.hasNext()) {
			Stream<T> s = iterator.next();
			stream = stream == null ? s : Stream.concat(stream, s);
		}
		return stream == null ? Stream.empty() : stream;
	}

	/**
	 * 构造一个简单的stream
	 * 
	 * @param <T>
	 * @param iterator
	 * @return
	 */
	public static <T> Stream<T> stream(Iterator<? extends T> iterator) {
		if (iterator == null) {
			return Stream.empty();
		}
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
		return StreamSupport.stream(spliterator, false);
	}

	/**
	 * 非并发流
	 * 
	 * @param <T>
	 * @param iterator
	 * @return
	 */
	public static <T> Stream<T> stream(Spliterator<T> iterator) {
		if (iterator == null) {
			return Stream.empty();
		}
		return StreamSupport.stream(iterator, false);
	}

	@SuppressWarnings("unchecked")
	public static <T> Stream<T> stream(Iterable<? extends T> iterable) {
		if (iterable == null) {
			return Stream.empty();
		}

		if (iterable instanceof Collection) {
			return ((Collection<T>) iterable).stream();
		}
		return stream(iterable.iterator());
	}

	public static <T> Stream<T> stream(CloseableIterator<? extends T> iterator) {
		if (iterator == null) {
			return Stream.empty();
		}

		Stream<T> stream = stream((Iterator<? extends T>) iterator);
		return stream.onClose(() -> iterator.close());
	}

	private Streams() {
	}
}
