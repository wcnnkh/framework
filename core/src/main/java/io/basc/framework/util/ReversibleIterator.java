package io.basc.framework.util;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * 连续的
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public interface ReversibleIterator<E> extends Iterator<E>, ReverseIterator<E> {

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
}
