package io.basc.framework.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

import io.basc.framework.util.Optional.SharedOptional;
import io.basc.framework.util.collect.CollectionUtils;
import lombok.NonNull;

public final class Bound<T> extends SharedOptional<T, RuntimeException> {
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

	/**
	 * 右包含
	 * 
	 * @param bound
	 * @param comparator
	 * @return
	 */
	public boolean leftContains(@NonNull T value, @NonNull Comparator<T> comparator) {
		if (isBounded()) {
			// 都有边界
			int compare = comparator.compare(get(), value);
			return inclusive ? compare <= 0 : compare < 0;
		}
		return true;
	}

	/**
	 * 右包含
	 * 
	 * @param value
	 * @param comparator
	 * @return
	 */
	public boolean rightContains(@NonNull T value, @NonNull Comparator<T> comparator) {
		if (isBounded()) {
			// 都有边界
			int compare = comparator.compare(get(), value);
			return inclusive ? compare >= 0 : compare > 0;
		}
		return true;
	}

	public <U> Bound<U> convert(Function<? super T, ? extends U> converter) {
		return new Bound<U>(converter.apply(orElse(null)), inclusive);
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