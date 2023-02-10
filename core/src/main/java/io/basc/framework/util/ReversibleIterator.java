package io.basc.framework.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 连续的
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public interface ReversibleIterator<E> extends Iterator<E>, ReverseIterator<E> {

	@Override
	default Stream<E> stream() {
		return XUtils.stream(this);
	}

	default ReversibleIterator<E> invert() {
		return new InvertedSuccessive<>(this);
	}

	@Override
	default void remove() {
		Iterator.super.remove();
	}

	@Override
	default void forEachRemaining(Consumer<? super E> action) {
		Iterator.super.forEachRemaining(action);
	}

	@SuppressWarnings("unchecked")
	public static <T> ReversibleIterator<T> of(Iterator<? extends T> iterator) {
		if (iterator instanceof ReversibleIterator) {
			return (ReversibleIterator<T>) iterator;
		}
		return new DefaultReversibleIterator<>(iterator);
	}

	public static <T> ReversibleIterator<T> of(List<? extends T> list) {
		return new DefaultReversibleIterator<>(list);
	}
}
