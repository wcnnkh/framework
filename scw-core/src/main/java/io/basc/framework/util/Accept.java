package io.basc.framework.util;

import io.basc.framework.lang.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface Accept<E> extends Predicate<E> {

	boolean accept(E e);

	@Override
	default boolean test(E t) {
		return accept(t);
	}

	/**
	 * 取反
	 * 
	 * @return
	 */
	default Accept<E> negate() {
		return new Accept<E>() {
			@Override
			public boolean accept(E e) {
				if(e == null) {
					return false;
				}
				return !Accept.this.accept(e);
			}
		};
	}

	/**
	 * 且
	 * 
	 * @param accept
	 * @return
	 */
	default Accept<E> and(Accept<? super E> accept) {
		Objects.requireNonNull(accept);
		return new Accept<E>() {
			@Override
			public boolean accept(E e) {
				return Accept.this.accept(e) && accept.accept(e);
			}
		};
	}

	/**
	 * 或
	 * 
	 * @param accept
	 * @return
	 */
	default Accept<E> or(Accept<? super E> accept) {
		Objects.requireNonNull(accept);
		return new Accept<E>() {

			@Override
			public boolean accept(E e) {
				return Accept.this.accept(e) || accept.accept(e);
			}
		};
	}

	/**
	 * 异或<br/>
	 * 如果a、b两个值不相同，则异或结果为1。如果a、b两个值相同，异或结果为0。
	 * 
	 * @param accept
	 * @return
	 */
	default Accept<E> xor(Accept<? super E> accept) {
		Objects.requireNonNull(accept);
		return new Accept<E>() {

			@Override
			public boolean accept(E e) {
				return Accept.this.accept(e) ^ accept.accept(e);
			}
		};
	}

	/**
	 * 
	 * @param items
	 * @return
	 */
	default boolean all(Iterable<? extends E> items) {
		for (E item : items) {
			if (!accept(item)) {
				return false;
			}
		}
		return true;
	}

	default boolean any(Iterable<? extends E> items) {
		for (E item : items) {
			if (accept(item)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	default boolean all(E... items) {
		return all(Arrays.asList(items));
	}

	@SuppressWarnings("unchecked")
	default boolean any(E... items) {
		return any(Arrays.asList(items));
	}

	@Nullable
	default <T extends E> T first(Iterable<T> items) {
		if (items == null) {
			return null;
		}

		for (T item : items) {
			if (accept(item)) {
				return item;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	default <T extends E> T first(T... items) {
		return first(Arrays.asList(items));
	}
}
