package io.basc.framework.util;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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
	default ReversibleIterator<E> limit(long start) {
		return limit(BigInteger.valueOf(start), null);
	}

	@Override
	default ReversibleIterator<E> limit(long start, long count) {
		return limit(BigInteger.valueOf(start), BigInteger.valueOf(count));
	}

	@Override
	default ReversibleIterator<E> limit(BigInteger start, BigInteger count) {
		return new FilterableCursor<>(this, start, count, null);
	}

	@Override
	default ReversibleIterator<E> limit(Predicate<? super E> start, Predicate<? super E> end) {
		return new FilterableCursor<>(this, start, end, null);
	}

	@Override
	default <U> ReversibleIterator<U> map(Function<? super E, ? extends U> mapper) {
		return new ConvertibleCursor<>(this, mapper, BigInteger.ZERO, null);
	}

	@Override
	default ReversibleIterator<E> filter(Predicate<? super E> predicate) {
		return new FilterableCursor<>(this, predicate, null);
	}

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
}
