package io.basc.framework.util;

import java.util.Objects;

public interface PredicateProcessor<T, E extends Throwable> {
	/**
	 * Evaluates this predicate on the given argument.
	 *
	 * @param t the input argument
	 * @return {@code true} if the input argument matches the predicate, otherwise
	 *         {@code false}
	 */
	boolean process(T t) throws E;

	default Processor<T, Boolean, E> toProcessor() {
		return (s) -> process(s);
	}

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
	default PredicateProcessor<T, E> and(PredicateProcessor<? super T, ? extends E> other) {
		Objects.requireNonNull(other);
		return (t) -> process(t) && other.process(t);
	}

	/**
	 * Returns a predicate that represents the logical negation of this predicate.
	 *
	 * @return a predicate that represents the logical negation of this predicate
	 */
	default PredicateProcessor<T, E> negate() {
		return (t) -> !process(t);
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
	default PredicateProcessor<T, E> or(PredicateProcessor<? super T, ? extends E> other) {
		Objects.requireNonNull(other);
		return (t) -> process(t) || other.process(t);
	}

	/**
	 * Returns a predicate that tests if two arguments are equal according to
	 * {@link Objects#equals(Object, Object)}.
	 *
	 * @param <T>       the type of arguments to the predicate
	 * @param targetRef the object reference with which to compare for equality,
	 *                  which may be {@code null}
	 * @return a predicate that tests if two arguments are equal according to
	 *         {@link Objects#equals(Object, Object)}
	 */
	static <T, X extends Throwable> PredicateProcessor<T, X> isEqual(Object targetRef) {
		return (null == targetRef) ? Objects::isNull : object -> targetRef.equals(object);
	}
}
