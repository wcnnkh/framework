package io.basc.framework.util;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Bound<T> extends DefaultOptional<T> {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings({ "rawtypes", "unchecked" }) //
	private static final Bound<?> UNBOUNDED = new Bound(null, true);

	private final boolean inclusive;

	private Bound(T value, boolean inclusive) {
		super(value);
		this.inclusive = inclusive;
	}

	public boolean isInclusive() {
		return inclusive;
	}

	@Override
	public <U> Bound<U> convert(Function<? super T, ? extends U> converter) {
		return new Bound<U>(converter.apply(getValue()), inclusive);
	}

	@Override
	public <U> Bound<U> map(Function<? super T, ? extends U> mapper) {
		return convert((e) -> e == null ? null : mapper.apply(e));
	}

	@Override
	public Bound<T> filter(Predicate<? super T> predicate) {
		return convert((e) -> (e != null && predicate.test(e)) ? e : null);
	}

	/**
	 * Creates an unbounded {@link Bound}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Bound<T> unbounded() {
		return (Bound<T>) UNBOUNDED;
	}

	/**
	 * Returns whether this boundary is bounded.
	 *
	 * @return
	 */
	public boolean isBounded() {
		return isPresent();
	}

	/**
	 * Creates a boundary including {@code value}.
	 *
	 * @param value must not be {@literal null}.
	 * @return
	 */
	public static <T> Bound<T> inclusive(T value) {
		Assert.notNull(value, "Value must not be null!");
		return new Bound<>(value, true);
	}

	/**
	 * Creates a boundary including {@code value}.
	 *
	 * @param value must not be {@literal null}.
	 * @return
	 */
	public static Bound<Integer> inclusive(int value) {
		return inclusive((Integer) value);
	}

	/**
	 * Creates a boundary including {@code value}.
	 *
	 * @param value must not be {@literal null}.
	 * @return
	 */
	public static Bound<Long> inclusive(long value) {
		return inclusive((Long) value);
	}

	/**
	 * Creates a boundary including {@code value}.
	 *
	 * @param value must not be {@literal null}.
	 * @return
	 */
	public static Bound<Float> inclusive(float value) {
		return inclusive((Float) value);
	}

	/**
	 * Creates a boundary including {@code value}.
	 *
	 * @param value must not be {@literal null}.
	 * @return
	 */
	public static Bound<Double> inclusive(double value) {
		return inclusive((Double) value);
	}

	/**
	 * Creates a boundary excluding {@code value}.
	 *
	 * @param value must not be {@literal null}.
	 * @return
	 */
	public static <T> Bound<T> exclusive(T value) {
		Assert.notNull(value, "Value must not be null!");
		return new Bound<>(value, false);
	}

	/**
	 * Creates a boundary excluding {@code value}.
	 *
	 * @param value must not be {@literal null}.
	 * @return
	 */
	public static Bound<Integer> exclusive(int value) {
		return exclusive((Integer) value);
	}

	/**
	 * Creates a boundary excluding {@code value}.
	 *
	 * @param value must not be {@literal null}.
	 * @return
	 */
	public static Bound<Long> exclusive(long value) {
		return exclusive((Long) value);
	}

	/**
	 * Creates a boundary excluding {@code value}.
	 *
	 * @param value must not be {@literal null}.
	 * @return
	 */
	public static Bound<Float> exclusive(float value) {
		return exclusive((Float) value);
	}

	/**
	 * Creates a boundary excluding {@code value}.
	 *
	 * @param value must not be {@literal null}.
	 * @return
	 */
	public static Bound<Double> exclusive(double value) {
		return exclusive((Double) value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return map(Object::toString).orElse("unbounded");
	}

	@Override
	public int hashCode() {
		return CollectionUtils.hashCode(Arrays.asList(inclusive, super.hashCode()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Bound) {
			Bound<?> other = (Bound<?>) obj;
			return (inclusive == other.inclusive) && super.equals(obj);
		}
		return false;
	}
}