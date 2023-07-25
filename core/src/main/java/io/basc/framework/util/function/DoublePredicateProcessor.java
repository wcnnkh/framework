package io.basc.framework.util.function;

import java.util.Objects;

public interface DoublePredicateProcessor<E extends Throwable> {
	/**
	 * Evaluates this predicate on the given argument.
	 *
	 * @param value the input argument
	 * @return {@code true} if the input argument matches the predicate, otherwise
	 *         {@code false}
	 */
	boolean process(double value) throws E;

	/**
	 * Returns a composed predicate that represents a short-circuiting logical AND
	 * of this predicate and another. When evaluating the composed predicate, if
	 * this predicate is {@code false}, then the {@code other} predicate is not
	 * evaluated.
	 *
	 * <p>
	 * Any exceptions thrown during evaluation of either predicate are relayed to
	 * the caller; if evaluation of this predicate throws an exception, the
	 * {@code other} predicate will not be evaluated.
	 *
	 * @param other a predicate that will be logically-ANDed with this predicate
	 * @return a composed predicate that represents the short-circuiting logical AND
	 *         of this predicate and the {@code other} predicate
	 * @throws NullPointerException if other is null
	 */
	default DoublePredicateProcessor<E> and(DoublePredicateProcessor<? extends E> other) {
		Objects.requireNonNull(other);
		return (value) -> process(value) && other.process(value);
	}

	/**
	 * Returns a predicate that represents the logical negation of this predicate.
	 *
	 * @return a predicate that represents the logical negation of this predicate
	 */
	default DoublePredicateProcessor<E> negate() {
		return (value) -> !process(value);
	}

	/**
	 * Returns a composed predicate that represents a short-circuiting logical OR of
	 * this predicate and another. When evaluating the composed predicate, if this
	 * predicate is {@code true}, then the {@code other} predicate is not evaluated.
	 *
	 * <p>
	 * Any exceptions thrown during evaluation of either predicate are relayed to
	 * the caller; if evaluation of this predicate throws an exception, the
	 * {@code other} predicate will not be evaluated.
	 *
	 * @param other a predicate that will be logically-ORed with this predicate
	 * @return a composed predicate that represents the short-circuiting logical OR
	 *         of this predicate and the {@code other} predicate
	 * @throws NullPointerException if other is null
	 */
	default DoublePredicateProcessor<E> or(DoublePredicateProcessor<? extends E> other) {
		Objects.requireNonNull(other);
		return (value) -> process(value) || other.process(value);
	}
}
