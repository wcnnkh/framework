package io.basc.framework.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface ReverseIterator<E> extends Streamy<E> {
	/**
	 * Returns {@code true} if this list iterator has more elements when traversing
	 * the list in the reverse direction. (In other words, returns {@code true} if
	 * {@link #previous} would return an element rather than throwing an exception.)
	 *
	 * @return {@code true} if the list iterator has more elements when traversing
	 *         the list in the reverse direction
	 */
	boolean hasPrevious();

	/**
	 * Returns the previous element in the list and moves the cursor position
	 * backwards. This method may be called repeatedly to iterate through the list
	 * backwards, or intermixed with calls to {@link #next} to go back and forth.
	 * (Note that alternating calls to {@code next} and {@code previous} will return
	 * the same element repeatedly.)
	 *
	 * @return the previous element in the list
	 * @throws NoSuchElementException if the iteration has no previous element
	 */
	E previous();

	/**
	 * Removes from the underlying collection the last element returned by this
	 * iterator (optional operation). This method can be called only once per call
	 * to {@link #next}. The behavior of an iterator is unspecified if the
	 * underlying collection is modified while the iteration is in progress in any
	 * way other than by calling this method.
	 *
	 * @implSpec The default implementation throws an instance of
	 *           {@link UnsupportedOperationException} and performs no other action.
	 *
	 * @throws UnsupportedOperationException if the {@code remove} operation is not
	 *                                       supported by this iterator
	 *
	 * @throws IllegalStateException         if the {@code next} method has not yet
	 *                                       been called, or the {@code remove}
	 *                                       method has already been called after
	 *                                       the last call to the {@code next}
	 *                                       method
	 */
	default void remove() {
		throw new UnsupportedOperationException("remove");
	}

	/**
	 * Performs the given action for each remaining element until all elements have
	 * been processed or the action throws an exception. Actions are performed in
	 * the order of iteration, if that order is specified. Exceptions thrown by the
	 * action are relayed to the caller.
	 *
	 * @implSpec
	 *           <p>
	 *           The default implementation behaves as if:
	 * 
	 *           <pre>{@code
	 *     while (hasNext())
	 *         action.accept(next());
	 * }</pre>
	 *
	 * @param action The action to be performed for each element
	 * @throws NullPointerException if the specified action is null
	 */
	default void forEachRemaining(Consumer<? super E> action) {
		Objects.requireNonNull(action);
		while (hasPrevious())
			action.accept(previous());
	}

	@Override
	default Stream<E> stream() {
		Iterator<E> iterator = new Iterator<E>() {
			@Override
			public boolean hasNext() {
				return hasPrevious();
			}

			@Override
			public E next() {
				return previous();
			}

			@Override
			public void remove() {
				ReverseIterator.this.remove();
			}

			@Override
			public void forEachRemaining(Consumer<? super E> action) {
				ReverseIterator.this.forEachRemaining(action);
			}
		};
		return XUtils.stream(iterator);
	}
}
